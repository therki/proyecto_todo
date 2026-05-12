package com.openwebinars.todo.controller;

import com.openwebinars.todo.model.Category;
import com.openwebinars.todo.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gestor")
@PreAuthorize("hasRole('GESTOR')")
@SecurityRequirement(name="basicAuth")
@Tag(name = "Gestor", description = "Endpoints del Gestor")
@AllArgsConstructor
public class GestorController {
    private final CategoryService categoryService;

    /**** CRUD CATEGORIA *****/

    /* Listar categorias */
    @Operation(summary = "Listar categorias", description = "Listar todas las categorias de la aplicación")
    @GetMapping("/categories")
    public List<Category> getAllCategories() {
        return categoryService.listCategories();
    }

    /* Obtener categoria */
    @Operation(summary = "Obtener categoria", description = "Obtener información de una categoría")
    @GetMapping("/category/{id}")
    public Category getCategory(
            @Parameter(description = "ID de la categoría", required = true)
            @PathVariable Long id) {
        return categoryService.findByID(id);
    }

    /* Crear categoría */
    @Operation(summary = "Crear categoria", description = "OCrear una nueva categoría")
    @PostMapping("/category")
    public ResponseEntity<Category> createCategory(
            @Parameter(description = "Datos de la categoría", required = true)
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                   description = "Datos de la nueva categoría",
                   required = true,
                   content = @Content(
                           mediaType = "application/json",
                           schema = @Schema(implementation = Category.class),
                           examples = @ExampleObject(
                                   value = "{ \"title\": \"Nueva Categoría\" }"
                           )
                   )
           )
            @org.springframework.web.bind.annotation.RequestBody Category category) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.save(category));
    }

    /* Editar categoria */
    @Operation(summary = "Crear categoria", description = "Editar una categoría existente")
    @PutMapping("/category/{id}")
    public ResponseEntity<Category> editCategory(
            @Parameter(description = "ID categoría a editar", required = true)
            @PathVariable Long id,
            @Parameter(description = "Datos de la categoría a editar", required = true)
            @org.springframework.web.bind.annotation.RequestBody Category  category) {
        return ResponseEntity.ok(categoryService.edit(id, category));
    }
    /* Borrar categoria */
    @Operation(summary = "Eliminar categoria", description = "Borrar una categoría por su ID")
    @DeleteMapping("/category/{id}")
    public ResponseEntity<?> deleteCategory(
            @Parameter(description = "ID categoría a eliminar", required = true)
            @PathVariable Long id) {
        categoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
