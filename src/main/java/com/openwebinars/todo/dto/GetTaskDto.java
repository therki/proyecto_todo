package com.openwebinars.todo.dto;

import com.openwebinars.todo.model.Task;
import com.openwebinars.todo.model.Task.Priority;
import com.openwebinars.todo.users.NewUserResponse;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public record GetTaskDto(
        Long id,
        String title,
        String description,
        boolean completed,
        LocalDateTime createdAt,
        LocalDateTime deadline,
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
                (t.getDeadline() != null) ? t.getDeadline().atStartOfDay() :null,
                t.getPriority(),
                (t.getCategory() != null) ? t.getCategory().getTitle() : "Sin categoría",
                t.getTags().stream()
                        .map(tag -> tag.getName())
                        .collect(Collectors.toSet()),
                NewUserResponse.of(t.getAuthor())
        );
    }
}