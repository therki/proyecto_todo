package com.openwebinars.todo.controller;

import com.openwebinars.todo.dto.EditTaskDto;
import com.openwebinars.todo.dto.GetTaskDto;
import com.openwebinars.todo.service.TaskService;
import com.openwebinars.todo.users.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/task/")
@RequiredArgsConstructor
@SecurityRequirement(name="basicAuth")
@Tag(name = "Tareas", description = "Endpoints para la gestión de tareas personales")
public class TaskController {

    private final TaskService taskService;

/* LISTAR */
    @Operation(
        summary = "Obtener todas las tareas del usuario",
        description = "Permite obtener todas las tares del usuario")
    @ApiResponse(description = "Lista de tareas del usuario",
        responseCode = "200",
        content = @Content(
                mediaType = "application/json",
                array=@ArraySchema(schema = @Schema(implementation = GetTaskDto.class))
        ))
    @GetMapping
    public List<GetTaskDto> getAll(@AuthenticationPrincipal User author){
        return taskService.findByAuthor(author)
            .stream()
            .map(GetTaskDto::of)
            .toList();}

/* OBTENER TAREA */
    @Operation(
            summary = "Obtener una tarea por ID",
            description = "Permite buscar una tarea mediante su ID")
    @ApiResponse(description = "Obtener tarea",
            responseCode = "200",
            content = @Content(
                    mediaType = "application/json",
                    array=@ArraySchema(schema = @Schema(implementation = GetTaskDto.class))
            ))
    @PreAuthorize("@OwnerCheck.check(#id, authentication.principal.getId())")
    @GetMapping("/{id}")
    public GetTaskDto getById(@PathVariable Long id){

        return GetTaskDto.of(taskService.findById(id));
    }

/* CREAR */
    @Operation(
            summary = "Crear una nueva tarea",
            description = "Crear una nueva tarea")
    @ApiResponse(description = "Tarea creada correctamente",
            responseCode = "201",
            content = @Content(
                    mediaType = "application/json",
                    array=@ArraySchema(schema = @Schema(implementation = GetTaskDto.class))
            ))
    @PostMapping
    public ResponseEntity<GetTaskDto> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "tarea a crear", required = true,
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation=EditTaskDto.class))
            )
            @RequestBody EditTaskDto cmd,
            @AuthenticationPrincipal User author){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GetTaskDto.of(taskService.save(cmd, author)));
    }

/* EDITAR */
    @Operation(
            summary = "Editar una tarea",
            description = "Editar una tarea existente")
    @PreAuthorize("@OwnerCheck.check(#id, authentication.principal.getId())")
    @ApiResponse(description = "Tarea actualizada correctamente",
            responseCode = "200",
            content = @Content(
                    mediaType = "application/json",
                    array=@ArraySchema(schema = @Schema(implementation = GetTaskDto.class))
            ))
    @PutMapping("/{id}")
    public GetTaskDto edit(
            @RequestBody EditTaskDto cmd,
            @PathVariable Long id){
        return GetTaskDto.of(taskService.edit(cmd, id));
    }

/* ELIMINAR */
    @Operation(
            summary = "Eliminar una tarea",
            description = "Eliminar una tarea a partir de su ID")
    @PreAuthorize("@OwnerCheck.check(#id, authentication.principal.getId())")
    @ApiResponse(description = "Tarea eliminada correctamente",
            responseCode = "204",
            content = @Content(
                    mediaType = "application/json",
                    array=@ArraySchema(schema = @Schema(implementation = GetTaskDto.class))
            ))
    @DeleteMapping("/{id}")
    public ResponseEntity<?>  delete( @PathVariable Long id){
        taskService.delete(id);
        return  ResponseEntity.noContent().build();
    }


}
