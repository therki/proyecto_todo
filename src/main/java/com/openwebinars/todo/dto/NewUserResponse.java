package com.openwebinars.todo.dto;

import com.openwebinars.todo.model.User;

public record NewUserResponse(Long id, String fullname, String username, String email, User.RoleType role) {

    public static NewUserResponse of(User user) {
        return new NewUserResponse(
                user.getId(),
                user.getFullname(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }

}