package com.openwebinars.todo.dto;

import com.openwebinars.todo.model.Tag;

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
