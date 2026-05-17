package com.openwebinars.todo.dto;

import com.openwebinars.todo.model.Task;
import com.openwebinars.todo.model.Task.Priority;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
/* DTO de salida - información de una tarea

Se necesita ya que al obtener la tarea, no se quiere mostrar el usuario
que la creo

*/
public record GetTaskDto(
        Long id,
        String title,
        String description,
        boolean completed,
        LocalDateTime createdAt,
        LocalDate deadline,
        Priority priority,
        String categoryName,
        Set<String> tags,
        NewUserResponse author
) {

    public static GetTaskDto of(Task t) {
        return new GetTaskDto(
                t.getId(),
                t.getTitle(),
                t.getDescription(),
                t.isCompleted(),
                t.getCreatedAt(),
                (t.getDeadline() != null) ? t.getDeadline() :null,
                t.getPriority(),
                (t.getCategory() != null) ? t.getCategory().getTitle() : "Sin categoría",
                t.getTags().stream()
                        .map(tag -> tag.getName())
                        .collect(Collectors.toSet()),
                NewUserResponse.of(t.getAuthor())
        );
    }
}