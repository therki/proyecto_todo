package com.openwebinars.todo.service;

import com.openwebinars.todo.dto.EditTaskDto;
import com.openwebinars.todo.dto.GetTaskDto;
import com.openwebinars.todo.error.TaskNotFoundException;
import com.openwebinars.todo.model.Category;
import com.openwebinars.todo.model.Tag;
import com.openwebinars.todo.model.Task;
import com.openwebinars.todo.repos.CategoryRepository;
import com.openwebinars.todo.repos.TagRepository;
import com.openwebinars.todo.repos.TaskRepository;
import com.openwebinars.todo.users.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    /* Listar todas las tareas */
    public List<Task> findAll(){
        List<Task> result = taskRepository.findAll();
        if(result.isEmpty()){
            throw new TaskNotFoundException();
        }
        return  result;
    }

    /* Obtener tareas por autor */
    public List<Task> findByAuthor(User autor){

        List<Task> result = taskRepository.findByAuthor(autor);
        if(result.isEmpty()){
            throw new TaskNotFoundException();
        }
        return  result;
    }

    /* Obtener tarea por ID */
    public Task findById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(()-> new TaskNotFoundException(id));
    }

    /* Crear nueva tarea */
    public Task save(EditTaskDto cmd, User autor) {
        Category category = categoryRepository.findById(cmd.categoryId())
                .orElse(null); // O podrías lanzar una excepción de CategoryNotFound

        List<Tag> tags = tagRepository.findAllById(cmd.tagsId());
        return taskRepository.save(
                Task.builder()
                        .title(cmd.title())
                        .description(cmd.description())
                        .deadline(cmd.deadline() != null ? LocalDate.from(cmd.deadline()) : null)
                        .priority(cmd.priority())
                        .completed(false)
                        .author(autor)
                        .category(category)
                        .tags(new HashSet<>(tags))
                        .build()
        );
    }

    /* Editar tarea */
    public Task edit(EditTaskDto cmd, Long id) {
        return taskRepository.findById(id)
                .map(t -> {
                    t.setTitle(cmd.title());
                    t.setDescription(cmd.description());
                    t.setCompleted(cmd.completed());
                    t.setDeadline(cmd.deadline() != null ? LocalDate.from(cmd.deadline()) : null);                    t.setPriority(cmd.priority());
                    return  taskRepository.save(t);
                }
        ).orElseThrow(()->new TaskNotFoundException(id));
    }

    /* Eliminar tarea */
    public void delete( Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }
        taskRepository.deleteById(id);
    }

    /* Obtener tareas por descripcion */
    public List<GetTaskDto> searchByDescription(String description, User autor){
        List<Task> result = taskRepository.findByDescriptionContainingIgnoreCaseAndAuthor(description, autor);
        if(result.isEmpty()){
            throw new TaskNotFoundException();
        }
        return result.stream()
                .map(GetTaskDto::of)
                .toList();
    }

    /* Obtener tareas por fecha de creacion */
    public List<GetTaskDto> searchByCreatedAt(LocalDate fecha, User autor){
        List<Task> result = taskRepository.findByCreatedAtAndAuthor(fecha, autor);
        if(result.isEmpty()){
            throw new TaskNotFoundException();
        }
        return result.stream()
                .map(GetTaskDto::of)
                .toList();
    }
    /* Obtener tareas por fecha límite */
    public List<GetTaskDto> searchByDeadline(LocalDate fecha, User autor){
        List<Task> result = taskRepository.findByDeadlineLessThanEqualAndAuthor(fecha, autor);
        if(result.isEmpty()){
            throw new TaskNotFoundException();
        }
        return result.stream()
                .map(GetTaskDto::of)
                .toList();
    }
    /* Obtener tareas completadas */
    public List<GetTaskDto> searchByCompleted(Boolean completed, User autor){
        List<Task> result = taskRepository.findByCompletedAndAuthor(completed, autor);
        if(result.isEmpty()){
            throw new TaskNotFoundException();
        }
        return result.stream()
                .map(GetTaskDto::of)
                .toList();
    }
    /* Obtener tareas por fecha límite */
    public List<GetTaskDto> searchByPriority(Task.Priority priority, User autor){
        List<Task> result = taskRepository.findByPriorityAndAuthor(priority, autor);
        if(result.isEmpty()){
            throw new TaskNotFoundException();
        }
        return result.stream()
                .map(GetTaskDto::of)
                .toList();
    }
    /* Obtener tareas por título */
    public List<GetTaskDto> searchByTitle(String title, User autor){
        List<Task> result = taskRepository.findByTitleContainingIgnoreCaseAndAuthor(title, autor);
        if(result.isEmpty()){
            throw new TaskNotFoundException();
        }
        return result.stream()
                .map(GetTaskDto::of)
                .toList();
    }

    public void updateCategoryBeforeDelete(Long oldCategoryId, Long newCategoryId) {
        // Buscamos todas las tareas de la categoría que va a desaparecer
        List<Task> tasks = taskRepository.findByCategoryId(oldCategoryId);

        if (!tasks.isEmpty()) {
            Category defaultCategory = categoryRepository.getReferenceById(newCategoryId);
            tasks.forEach(task -> task.setCategory(defaultCategory));
            taskRepository.saveAll(tasks);
        }
    }
    /* Obtener tarea de un usuario por nombre de etiqueta*/
    public List<GetTaskDto> searchByTag(String tagName, User user){
        List<Task> tasks = taskRepository.findByTagNameAndAuthor(tagName, user);
        if(tasks.isEmpty()){
            throw new TaskNotFoundException("No hay tareas con la etiqueta: " + tagName);
        }
        return tasks.stream()
                .map(GetTaskDto::of)
                .toList();
    }
    public List<GetTaskDto> searchByTagList(List<String> tagNames, User user){
        List<Task> tasks = taskRepository.findByTagNamesAndAuthor(tagNames, user);
        if(tasks.isEmpty()){
            throw new TaskNotFoundException("No hay tareas con las etiquetas: " + tagNames);
        }
        return tasks.stream()
                .map(GetTaskDto::of)
                .toList();
    }
    /* Listar tareas filtradas por parametros */
    public List<GetTaskDto> searchTasks(String title,String description,Task.Priority priority,
            Boolean completed,LocalDate deadline,User autor, String category) {
        List<Task> tasks;

        if (title != null && !title.isBlank()) {
            tasks = taskRepository.findByTitleContainingIgnoreCaseAndAuthor(title, autor);
        } else if (description != null && !description.isBlank()) {
            tasks = taskRepository.findByDescriptionContainingIgnoreCaseAndAuthor(description, autor);
        } else if (priority != null) {
            tasks = taskRepository.findByPriorityAndAuthor(priority, autor);
        } else if (completed != null) {
            tasks = taskRepository.findByCompletedAndAuthor(completed, autor);
        } else if (deadline != null) {
            tasks = taskRepository.findByDeadlineLessThanEqualAndAuthor(deadline, autor);
        } else if (category != null) {
        Category cat = categoryRepository.findByTitle(category);
        tasks = taskRepository.findByCategoryAndAuthor(cat, autor);
    }  else {
            // Si no se pasa ningún filtro, devolvemos todas las tareas del autor
            tasks = taskRepository.findByAuthor(autor);
        }

        if (tasks.isEmpty()) {
            throw new TaskNotFoundException("No se encontraron tareas con los filtros aplicados.");
        }

        return tasks.stream()
                .map(GetTaskDto::of)
                .toList();
    }


    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }
}
