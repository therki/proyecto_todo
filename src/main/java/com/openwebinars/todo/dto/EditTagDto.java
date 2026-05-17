package com.openwebinars.todo.dto;
/* DTO para edición de etiqueta

Se necesita ya que al editar, SpringBoot confunde con Category
y devuelve la categoria antes de editar (con el valor inicial)

- No expone id
- Permite recibir solo campos modificables
*/
public record EditTagDto(String name) {
}
