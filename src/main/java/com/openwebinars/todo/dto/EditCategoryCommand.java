package com.openwebinars.todo.dto;
/* Se necesita ya que al editar, SpringBoot confunde con Category
y devuelve la categoria antes de editar (con el valor inicial)*/
public record EditCategoryCommand(String title) {}

