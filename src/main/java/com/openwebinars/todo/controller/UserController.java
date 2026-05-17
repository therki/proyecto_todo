package com.openwebinars.todo.controller;

import com.openwebinars.todo.dto.EditTaskDto;
import com.openwebinars.todo.model.Category;
import com.openwebinars.todo.model.Task;
import com.openwebinars.todo.service.CategoryService;
import com.openwebinars.todo.service.TagService;
import com.openwebinars.todo.service.TaskService;
import com.openwebinars.todo.users.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
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
@PreAuthorize("hasRole('USUARIO')")
@SecurityRequirement(name="basicAuth")
@Tag(name = "Usuario", description = "Endpoints del Usuario")
@RequiredArgsConstructor
public class UserController {
    private final TaskService taskService;
    private final UserService userService;
    private final TagService tagService;
    private final CategoryService categoryService;

    /*** CRUD TAREA ****/
    /* Listar tareas*/
    @Operation(summary = "Listar mis tareas", description = "Recupera todas las tareas del usuario autenticado")
    @GetMapping("/tasks")
    public List<Task> getMyTasks(@Parameter(hidden = true) @AuthenticationPrincipal User user) {
        return taskService.findByAuthor(user);
    }
    /* Obtener tarea */
    @Operation(summary = "Obtener tarea por ID", description = "Obtiene los detalles de una tarea específica")
    @GetMapping("/task/{id}")
    public Task getById(
            @Parameter(description = "ID de la tarea", required = true)
            @PathVariable Long id) {
        return taskService.findById(id);
    }

    /* Crear tarea */
    @Operation(summary = "Crear tarea", description = "Crea una nueva tarea asignándola al usuario actual")
    @PostMapping("/task")
    public ResponseEntity<Task> createTask(
            @Parameter(description = "Datos de la tarea a crear", required = true)
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la nueva tarea",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EditTaskDto.class),
                            examples = @ExampleObject(
                                    value = "{ \"title\": \"Nueva tarea\", \"description\": \"Descripción de la tarea\", \"completed\": false, \"deadline\": \"2025-12-31T23:59\", \"priority\": \"MEDIA\", \"categoryId\": 1, \"tagsId\": [1,2] }"
                            )
                    )
            )
            @RequestBody EditTaskDto dto,
            @Parameter(hidden = true)
            @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.save(dto, user));
    }

    /* Editar tarea */
    @Operation(summary = "Editar tarea", description = "Modifica los datos de una tarea existente")
    @PutMapping("/task/{id}")
    public Task updateTask(
            @Parameter(description = "ID de la tarea a editar", required = true)
            @PathVariable Long id,
            @Parameter(description = "Datos actualizados de la tarea", required = true)
            @RequestBody EditTaskDto dto) {
        return taskService.edit(dto, id);
    }

    /* Eliminar tarea */
    @Operation(summary = "Eliminar tarea", description = "Borra permanentemente una tarea")
    @DeleteMapping("/task/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(
            @Parameter(description = "ID de la tarea a eliminar", required = true)
            @PathVariable Long id) {
        taskService.delete(id);
    }
    /** Obtener tareas filtradas */
    /* Obtener tarea por descripcion*/
    @Operation(summary = "Obtener tarea por descripción", description = "Obtiene el listado de tareas dada una descripción")
    @GetMapping("/task/search-by-description")
    public List<Task> getByDescription(
            @Parameter(description = "Descripción de la tarea", required = true)
            @RequestParam String description,
            @Parameter( hidden = true) @AuthenticationPrincipal User user) {
        return taskService.searchByDescription(description, user);
    }
    /* Obtener tarea por prioridad*/
    @Operation(summary = "Obtener tarea por prioridad", description = "Obtiene el listado de tareas con una prioridad específica")
    @GetMapping("/task/search-by-priority")
    public List<Task> getByPriority(
            @Parameter(description = "Prioridad de la tarea", required = true)
            @RequestParam Task.Priority priority,
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        return taskService.searchByPriority(priority, user);
    }
    /* Obtener tarea por estado completado*/
    @Operation(summary = "Obtener tarea por estado", description = "Obtiene el listado de tareas con estado completado / no completado")
    @GetMapping("/task/search-completed")
    public List<Task> getByStatus(
            @Parameter(description = "Estado de la tarea (true/false)", required = true)
            @RequestParam Boolean completed,
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        return taskService.searchByCompleted(completed, user);
    }
    /* Obtener tarea por estado completado*/
    @Operation(summary = "Obtener tarea por fecha límite (formato YYYY-MM-DD)", description = "Obtiene el listado de tareas filtradas por fecha límite")
    @GetMapping("/task/search-by-deadline")
    public List<Task> getByDeadline(
            @Parameter(description = "Fecha límite de la tarea", required = true)
            @RequestParam String deadline,
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        return taskService.searchByDeadline(LocalDate.parse(deadline), user);
    }
    @Operation(summary = "Obtener tarea por estado", description = "Obtiene el listado de tareas con estado completado / no completado")
    @GetMapping("/task/search-by-title")
    public List<Task> getByTitle(
            @Parameter(description = "Título de la tarea", required = true)
            @RequestParam String title,
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        return taskService.searchByTitle(title, user);
    }
    @Operation(summary = "Buscar tareas por etiqueta",
            description = "Obtiene todas las tareas del usuario que tienen una etiqueta")
    @GetMapping("/task/search-by-tag")
    public List<Task> getTasksByTag(
            @Parameter(description = "Nombre de la etiqueta", required = true)
            @RequestParam String tagName,
            @Parameter(hidden = true)
            @AuthenticationPrincipal User user) {
        return taskService.searchByTag(tagName, user);
    }

    @Operation(summary = "Buscar tareas por etiquetas",
            description = "Obtiene todas las tareas del usuario que tienen alguna de las etiquetas")
    @GetMapping("/task/search-by-tags")
    public List<Task> getTasksByTagList(
            @Parameter(description = "Nombre de las etiquetas", required = true)
            @RequestParam List<String> tagNames,
            @Parameter(hidden = true)
            @AuthenticationPrincipal User user) {
        return taskService.searchByTagList(tagNames, user);
    }
    /*** CRUD TAG */
    /* Listar etiquetas */
    @Operation(summary = "Listar etiquetas", description = "Listar etiquetas del usuario")
    @GetMapping("/tags")
    public List<com.openwebinars.todo.model.Tag> listTags(
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        return tagService.findAllByUser(user);
    }

    /* Obtener etiqueta */
    @Operation(summary = "Listar etiquetas", description = "Listar etiquetas del usuario")
    @GetMapping("/tag/{name}")
    public com.openwebinars.todo.model.Tag getTag(
            @Parameter(description = "Nombre de la etiqueta", required = true)
            @PathVariable String name,
            @Parameter(hidden = true)
            @AuthenticationPrincipal User user) {
        return tagService.findByNameAndUser(name, user);
    }

    /* Crear etiqueta */
    @Operation(summary = "Crear etiqueta", description = "Crear nueva etiqueta")
    @PostMapping("/tag")
    public ResponseEntity<com.openwebinars.todo.model.Tag> createTag(
            @Parameter(description = "Datos de la neva etiqueta", required = true)
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la etiqueta",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{ \"name\": \"urgente\" }"
                            )
                    )
            )
            @RequestBody com.openwebinars.todo.model.Tag tag,
            @Parameter(hidden = true)
            @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tagService.save(tag, user));
    }
    /* Editar etiqueta */
    @Operation(summary = "Editar etiqueta", description = "Editar etiqueta existente")
    @PutMapping("/tag/{id}")
    public com.openwebinars.todo.model.Tag editTag(
            @Parameter(description = "ID de la etiqueta", required = true)
            @PathVariable Long id,
            @Parameter(description = "Datos actualizados de la etiqueta", required = true)
            @RequestBody com.openwebinars.todo.model.Tag tag,
            @Parameter(hidden = true)
            @AuthenticationPrincipal User user) {
        return tagService.edit(id, tag, user);
    }
    /* Eliminar etiqueta */
    @Operation(summary = "Eliminar etiqueta", description = "Borra una etiqueta ")
    @DeleteMapping("/tag/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTag(
            @Parameter(description = "ID de la etiqueta", required = true)
            @PathVariable Long id,
            @Parameter(hidden = true)
            @AuthenticationPrincipal User user) {
        tagService.deleteById(id, user);
    }

    /** LIstar categorias */
    /* Listar categorias */
    @Operation(summary = "Listar etiquetas", description = "Listar etiquetas del usuario")
    @GetMapping("/categories")
    public List<Category> listCategories() {
        return categoryService.listCategories();
    }
    /* Editar datos propios del usuario */
    @Operation(summary = "Editar información de usuario", description = "Actualizar información propia del usuario")
    @PutMapping("/edit-profile")
    public User editSelf(
            @Parameter(hidden = true)
            @AuthenticationPrincipal User user,
            @Parameter(description = "Datos actualizados del usuario", required = true)
            @RequestBody EditUserCommand userInfo) {

        Long userId = user.getId();
        return userService.updatePartialUser(userId, userInfo);
    }

}
