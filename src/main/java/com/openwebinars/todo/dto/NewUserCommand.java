package com.openwebinars.todo.dto;

import com.openwebinars.todo.model.User;
/* DTO de entrada para la creación de un usuario

Se necesita ya que al editar, SpringBoot confunde con Category
y devuelve la categoria antes de editar (con el valor inicial)

- No incluye campos autogenerados (id)
- Solo solicita los campos estrictamente necesarios para crear un usuario
*/
public record NewUserCommand(String username, String fullname, String email, String password, User.RoleType role) {
}