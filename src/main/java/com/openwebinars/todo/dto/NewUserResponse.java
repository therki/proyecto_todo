package com.openwebinars.todo.dto;

import com.openwebinars.todo.model.User;
/* DTO de salida para la obtención de usuario

Devuelve informción del usuario evitando la información sensible como la contraseña

*/
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