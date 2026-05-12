package com.openwebinars.todo.controller;

import com.openwebinars.todo.dto.EditTaskDto;
import com.openwebinars.todo.model.Category;
import com.openwebinars.todo.model.Task;
import com.openwebinars.todo.repos.TagRepository;
import com.openwebinars.todo.service.CategoryService;
import com.openwebinars.todo.service.TagService;
import com.openwebinars.todo.service.TaskService;
import com.openwebinars.todo.users.NewUserCommand;
import com.openwebinars.todo.users.NewUserResponse;
import com.openwebinars.todo.users.User;
import com.openwebinars.todo.users.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/user")
@PreAuthorize("hasRole('USER')")
@SecurityRequirement(name="basicAuth")
@Tag(name = "Usuario", description = "Endpoints del Usuario")
@RequiredArgsConstructor
public class UserController {
    private final TaskService taskService;
    private final UserService userService;
    private final TagService tagService;
    private CategoryService categoryService;

//    @PostMapping("/auth/register")
//    public ResponseEntity<NewUserResponse> createUser(@RequestBody NewUserCommand cmd) {
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(NewUserResponse.of(userService.register(cmd)));
//    }
    /*** CRUD TAREA ****/
    /* Listar tareas*/
    @Operation(summary = "Listar mis tareas", description = "Recupera todas las tareas del usuario autenticado")
    @GetMapping("/tasks")
    public List<Task> getMyTasks(@AuthenticationPrincipal User user) {
        return taskService.findByAuthor(user);
    }
    /* Obtener tarea */
    @Operation(summary = "Obtener tarea por ID", description = "Obtiene los detalles de una tarea específica")
    @GetMapping("/task/{id}")
    public Task getById(@PathVariable Long id) {
        return taskService.findById(id);
    }

    /* Crear tarea */
    @Operation(summary = "Crear tarea", description = "Crea una nueva tarea asignándola al usuario actual")
    @PostMapping("/task")
    public ResponseEntity<Task> createTask(
            @RequestBody EditTaskDto dto,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.save(dto, user));
    }

    /* Editar tarea */
    @Operation(summary = "Editar tarea", description = "Modifica los datos de una tarea existente")
    @PutMapping("/task/{id}")
    public Task updateTask(@PathVariable Long id, @RequestBody EditTaskDto dto) {
        return taskService.edit(dto, id);
    }

    /* Eliminar tarea */
    @Operation(summary = "Eliminar tarea", description = "Borra permanentemente una tarea")
    @DeleteMapping("/task/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long id) {
        taskService.delete(id);
    }
    /** Obtener tareas filtradas */
    /* Obtener tarea por descripcion*/
    @Operation(summary = "Obtener tarea por descripción", description = "Obtiene el listado de tareas dada una descripción")
    @GetMapping("/task/search-by-description")
    public List<Task> getByDescription(@RequestParam String description, @AuthenticationPrincipal User user) {
        return taskService.searchByDescription(description, user);
    }
    /* Obtener tarea por prioridad*/
    @Operation(summary = "Obtener tarea por prioridad", description = "Obtiene el listado de tareas con una prioridad específica")
    @GetMapping("/task/search-by-priority")
    public List<Task> getByPriority(@RequestParam Task.Priority priority, @AuthenticationPrincipal User user) {
        return taskService.searchByPriority(priority, user);
    }
    /* Obtener tarea por estado completado*/
    @Operation(summary = "Obtener tarea por estado", description = "Obtiene el listado de tareas con estado completado / no completado")
    @GetMapping("/task/search-completed")
    public List<Task> getByStatus(@RequestParam Boolean completed, @AuthenticationPrincipal User user) {
        return taskService.searchByCompleted(completed, user);
    }
    /* Obtener tarea por estado completado*/
    @Operation(summary = "Obtener tarea por fecha límite", description = "Obtiene el listado de tareas filtradas por fecha límite")
    @GetMapping("/task/search-by-deadline")
    public List<Task> getByDeadline(@RequestParam String deadline, @AuthenticationPrincipal User user) {
        return taskService.searchByDeadline(LocalDate.parse(deadline), user);
    }
    @Operation(summary = "Obtener tarea por estado", description = "Obtiene el listado de tareas con estado completado / no completado")
    @GetMapping("/task/search-by-title")
    public List<Task> getByTitle(@RequestParam String title, @AuthenticationPrincipal User user) {
        return taskService.searchByTitle(title, user);
    }
    /*** CRUD TAG */
    /* Listar etiquetas */
    @Operation(summary = "Listar etiquetas", description = "Listar etiquetas del usuario")
    @GetMapping("/tags")
    public List<com.openwebinars.todo.model.Tag> listTags(@AuthenticationPrincipal User user) {
        return tagService.findAll();
    }

    /* Obtener etiqueta */
    @Operation(summary = "Listar etiquetas", description = "Listar etiquetas del usuario")
    @GetMapping("/tag/{name}")
    public com.openwebinars.todo.model.Tag getTag(String name) {
        return tagService.findByName(name);
    }

    /* Crear etiqueta */
    @Operation(summary = "Crear etiqueta", description = "Crear nueva etiqueta")
    @PostMapping("/tag")
    public ResponseEntity<com.openwebinars.todo.model.Tag> createTag(@RequestBody com.openwebinars.todo.model.Tag tag) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tagService.save(tag));
    }
    /* Editar etiqueta */
    @Operation(summary = "Editar etiqueta", description = "Editar etiqueta existente")
    @PutMapping("/tag/{id}")
    public com.openwebinars.todo.model.Tag editTag(@PathVariable Long id, @RequestBody com.openwebinars.todo.model.Tag tag) {
        return tagService.edit(id, tag);
    }
    /* Eliminar etiqueta */
    @Operation(summary = "Eliminar etiqueta", description = "Borra una etiqueta ")
    @DeleteMapping("/tag/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTag(@PathVariable Long id) {
        tagService.deleteById(id);
    }
    /** LIstar categorias */
    /* Listar categorias */
    @Operation(summary = "Listar etiquetas", description = "Listar etiquetas del usuario")
    @GetMapping("/tags")
    public List<Category> listCategories() {
        return categoryService.listCategories();
    }
    /* Añadir etiqueta a una tarea */
    @Operation(summary = "Añadir etiqueta", description = "Añadir etiqueta a una tarea existente")
    @GetMapping("/task/{id}/tag/{tagId}")
    public ResponseEntity<Task> updateTaskTag() {
        return taskService.listCategories();
    }
}
