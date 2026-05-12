package com.openwebinars.todo.users;

public record NewUserResponse(Long id, String username, String email, User.RoleType role) {

    public static NewUserResponse of(User user) {
        return new NewUserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }

}