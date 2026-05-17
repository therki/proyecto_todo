package com.openwebinars.todo.controller;

import com.openwebinars.todo.dto.*;
import com.openwebinars.todo.model.Category;
import com.openwebinars.todo.model.Task;
import com.openwebinars.todo.model.User;
import com.openwebinars.todo.service.CategoryService;
import com.openwebinars.todo.service.TagService;
import com.openwebinars.todo.service.TaskService;
import com.openwebinars.todo.service.UserService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @GetMapping("/task")
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
    public ResponseEntity<GetTaskDto> createTask(
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
        Task nuevaTarea = taskService.save(dto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(GetTaskDto.of(nuevaTarea));
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
    @Operation(summary = "Eliminar tag de una tarea", description = "Elimina una etiqueta específica de una tarea")
    @DeleteMapping("/task/{id}/tags/{tagId}")
    public ResponseEntity<GetTaskDto> removeTagFromTask(
            @Parameter(description = "ID de la tarea", required = true)
            @PathVariable Long id,
            @Parameter(description = "ID de la etiqueta a eliminar", required = true)
            @PathVariable Long tagId,
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {

        Task task = taskService.findById(id);
        task.getTags().removeIf(tag -> tag.getId().equals(tagId));

        Task updatedTask = taskService.saveTask(task);  // <- Cambiado: solo task, sin GetTaskDto.of()
        return ResponseEntity.ok(GetTaskDto.of(updatedTask));
    }

    @Operation(summary = "Asignar tags a una tarea", description = "Añade una o varias etiquetas a una tarea existente")
    @PostMapping("/task/{id}/tags")
    public ResponseEntity<GetTaskDto> addTagsToTask(
            @Parameter(description = "ID de la tarea", required = true)
            @PathVariable Long id,
            @Parameter(description = "IDs de las etiquetas a añadir", required = true)
            @RequestBody List<Long> tagIds,
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {

        Task task = taskService.findById(id);
        for (Long tagId : tagIds) {
            com.openwebinars.todo.model.Tag tag = tagService.findById(tagId);
            if (tag.getUser().getId().equals(user.getId())) {
                task.getTags().add(tag);
            }
        }

        Task updatedTask = taskService.saveTask(task);  // <- Cambiado: solo task, sin GetTaskDto.of()
        return ResponseEntity.ok(GetTaskDto.of(updatedTask));
    }
    /** Obtener tareas filtradas */
    /* Obtener tarea por descripcion*/
    @Operation(summary = "Obtener tarea por descripción", description = "Obtiene el listado de tareas dada una descripción")
    @GetMapping("/task/search-by-description")
    public List<GetTaskDto> getByDescription(
            @Parameter(description = "Descripción de la tarea", required = true)
            @RequestParam String description,
            @Parameter( hidden = true) @AuthenticationPrincipal User user) {
        return taskService.searchByDescription(description, user);
    }
    /* Obtener tarea por prioridad*/
    @Operation(summary = "Obtener tarea por prioridad", description = "Obtiene el listado de tareas con una prioridad específica")
    @GetMapping("/task/search-by-priority")
    public List<GetTaskDto> getByPriority(
            @Parameter(description = "Prioridad de la tarea", required = true)
            @RequestParam Task.Priority priority,
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        return taskService.searchByPriority(priority, user);
    }
    /* Obtener tarea por estado completado*/
    @Operation(summary = "Obtener tarea por estado", description = "Obtiene el listado de tareas con estado completado / no completado")
    @GetMapping("/task/search-completed")
    public List<GetTaskDto> getByStatus(
            @Parameter(description = "Estado de la tarea (true/false)", required = true)
            @RequestParam Boolean completed,
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        return taskService.searchByCompleted(completed, user);
    }
    /* Obtener tarea por estado completado*/
    @Operation(summary = "Obtener tarea por fecha límite (formato YYYY-MM-DD)", description = "Obtiene el listado de tareas filtradas por fecha límite")
    @GetMapping("/task/search-by-deadline")
    public List<GetTaskDto> getByDeadline(
            @Parameter(description = "Fecha límite de la tarea", required = true)
            @RequestParam String deadline,
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        return taskService.searchByDeadline(LocalDate.parse(deadline), user);
    }
    @Operation(summary = "Obtener tarea por titulo", description = "Obtiene el listado de tareas filtradas por titulo")
    @GetMapping("/task/search-by-title")
    public List<GetTaskDto> getByTitle(
            @Parameter(description = "Título de la tarea", required = true)
            @RequestParam String title,
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        return taskService.searchByTitle(title, user);
    }
    @Operation(summary = "Obtener tarea por categoria", description = "Obtiene el listado de tareas por categoria")
    @GetMapping("/task/search-by-category")
    public List<GetTaskDto> searchByCategory(@RequestParam String title, @AuthenticationPrincipal User user) {
        return taskService.searchByTitle(title, user);
    }
    @Operation(summary = "Buscar tareas por etiqueta",
            description = "Obtiene todas las tareas del usuario que tienen una etiqueta")
    @GetMapping("/task/by-tag")
    public List<GetTaskDto> getTasksByTag(
            @Parameter(description = "Nombre de la etiqueta", required = true)
            @RequestParam String tagName,
            @Parameter(hidden = true)
            @AuthenticationPrincipal User user) {
        return taskService.searchByTag(tagName, user);
    }

    @Operation(summary = "Buscar tareas por etiquetas",
            description = "Obtiene todas las tareas del usuario que tienen alguna de las etiquetas")
    @GetMapping("/task/by-tags")
    public List<GetTaskDto> getTasksByTagList(
            @Parameter(description = "Nombre de las etiquetas", required = true)
            @RequestParam List<String> tagNames,
            @Parameter(hidden = true)
            @AuthenticationPrincipal User user) {
        return taskService.searchByTagList(tagNames, user);
    }
    /* Búsqueda Unificada*/
    @Operation(summary = "Buscar tareas", description = "Busca tareas  utilizando filtros opcionales")
    @GetMapping("/task/search")
    public List<GetTaskDto> searchTasks(
            @Parameter(description = "Filtrar por título")
            @RequestParam(required = false) String title,
            @Parameter(description = "Filtrar por descripción")
            @RequestParam(required = false) String description,
            @Parameter(description = "Filtrar por prioridad (BAJA, MEDIA, ALTA)")
            @RequestParam(required = false) Task.Priority priority,
            @Parameter(description = "Filtrar por estado completado (true/false)")
            @RequestParam(required = false) Boolean completed,
            @Parameter(description = "Filtrar por fecha límite (formato YYYY-MM-DD)")
            @RequestParam(required = false) String deadline,
            @Parameter(description = "Filtrar por categoria")
            @RequestParam(required = false) String category,
            @Parameter(hidden = true)
            @AuthenticationPrincipal User user) {

        LocalDate deadlineDate = (deadline != null && !deadline.isBlank()) ? LocalDate.parse(deadline) : null;

        return taskService.searchTasks(title, description, priority, completed, deadlineDate, user,category);
    }
    /*** CRUD TAG */
    /* Listar etiquetas */
    @Operation(summary = "Listar etiquetas", description = "Listar etiquetas del usuario")
    @GetMapping("/tags")
    public List<GetTagDto> listTags(
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        return tagService.findAllByUser(user);
    }

    /* Obtener etiqueta */
    @Operation(summary = "Listar etiquetas", description = "Listar etiquetas del usuario")
    @GetMapping("/tag/{name}")
    public GetTagDto getTag(
            @Parameter(description = "Nombre de la etiqueta", required = true)
            @PathVariable String name,
            @Parameter(hidden = true)
            @AuthenticationPrincipal User user) {
        com.openwebinars.todo.model.Tag tag = tagService.findByNameAndUser(name, user);
        return GetTagDto.of(tag);
    }

    /* Crear etiqueta */
    @Operation(summary = "Crear etiqueta", description = "Crear nueva etiqueta")
    @PostMapping("/tag")
    public ResponseEntity<GetTagDto> createTag(
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
            @RequestBody EditTagDto tag,
            @Parameter(hidden = true)
            @AuthenticationPrincipal User user) {
        com.openwebinars.todo.model.Tag nuevaTag = tagService.save(tag, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(GetTagDto.of(nuevaTag));
    }

    /* Editar etiqueta */
    @Operation(summary = "Editar etiqueta", description = "Editar etiqueta existente")
    @PutMapping("/tag/{id}")
    public GetTagDto editTag(
            @Parameter(description = "ID de la etiqueta", required = true)
            @PathVariable Long id,
            @Parameter(description = "Datos actualizados de la etiqueta", required = true)
            @RequestBody EditTagDto tag,
            @Parameter(hidden = true)
            @AuthenticationPrincipal User user) {
        com.openwebinars.todo.model.Tag tagEditado = tagService.edit(id, tag, user);
        return GetTagDto.of(tagEditado);
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
    @PutMapping("/profile")
    public NewUserResponse editSelf(
            @Parameter(hidden = true)
            @AuthenticationPrincipal User user,
            @Parameter(description = "Datos actualizados del usuario", required = true)
            @RequestBody EditUserCommand userInfo) {

        Long userId = user.getId();
        User usuarioActualizado = userService.updatePartialUser(userId, userInfo);
        return NewUserResponse.of(usuarioActualizado);
    }

    /* Dashboard del usuario */
    @Operation(summary = "Editar información de usuario", description = "Dashboard del usuario")
    @PutMapping("/dashboard")
    public Map<String, Object>  userDashboard(
            @Parameter(hidden = true)
            @AuthenticationPrincipal User user
            ) {

        Long userId = user.getId();

        Integer totalTareas = taskService.findByAuthor(user).toArray().length;
        Map<String, Object> response = new HashMap<>();
        response.put("totalTareas", totalTareas);
        Integer totalTags = tagService.findAllByUser(user).toArray().length;
        response.put("totalTags", totalTags);

        return response;
    }

}
