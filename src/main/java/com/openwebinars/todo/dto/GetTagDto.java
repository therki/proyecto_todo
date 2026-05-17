package com.openwebinars.todo.dto;

import com.openwebinars.todo.model.Tag;
import com.openwebinars.todo.model.Task;
import com.openwebinars.todo.users.NewUserResponse;

import java.util.stream.Collectors;

public record GetTagDto(
        Long id, String name, NewUserResponse user
) {
    public static GetTagDto of(Tag t) {
        return new GetTagDto(
                t.getId(),
                t.getName(),
                NewUserResponse.of(t.getUser())
        );
    }
}
