package com.openwebinars.todo.dto;
/* DTO para edición de usuario
 Solo solicita los campos modificables
 - No incluye campos autogenerados (id)
*/
public record EditUserCommand(String username,String fullname,String email,String password) {
}
