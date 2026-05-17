package com.openwebinars.todo.dto;

import com.openwebinars.todo.model.User;

public record NewUserCommand(String username, String fullname, String email, String password, User.RoleType role) {
}