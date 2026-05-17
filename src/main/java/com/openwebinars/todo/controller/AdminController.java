package com.openwebinars.todo.controller;

import com.openwebinars.todo.dto.EditCategoryCommand;
import com.openwebinars.todo.dto.EditUserCommand;
import com.openwebinars.todo.dto.NewUserCommand;
import com.openwebinars.todo.dto.NewUserResponse;
import com.openwebinars.todo.model.Category;
import com.openwebinars.todo.model.User;
import com.openwebinars.todo.service.CategoryService;
import com.openwebinars.todo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name="basicAuth")
@Tag(name = "Admin", description = "Endpoints de administración")
@AllArgsConstructor
public class AdminController {
    private final UserService userService;

    private final CategoryService categoryService;

    /* Cambiar Usuario - Gestor */
    @Operation(summary = "Promocionar usuario a gestor", description = "Cambia el rol de un usuario a GESTOR")
    @PutMapping("/user/{id}/promote")
    public ResponseEntity<NewUserResponse> promoteToGestor(
            @Parameter(description = "ID del usuario a promocionar", required = true)
            @PathVariable Long id) {
        User user = userService.changeRole(id, User.RoleType.GESTOR);
        return ResponseEntity.ok(NewUserResponse.of(user));
    }
    /* Cambiar Gestor - Usuario */
    @Operation(summary = "Degradar usuario gestor a usuario", description = "Cambia el rol de un usuario a USUARIO")
    @PutMapping("/user/{id}/demote")
    public ResponseEntity<NewUserResponse> demoteToUser(
            @Parameter(description = "ID del usuario a degradar", required = true)
            @PathVariable Long id) {
        User user = userService.changeRole(id, User.RoleType.USUARIO);
        return ResponseEntity.ok(NewUserResponse.of(user));       }
    /****  CRUD USUARIO ****/
    /* Listar usuarios */
    @Operation(summary = "Listar usuarios", description = "Listar todos los usuarios de la aplicación")
    @GetMapping("/users")
    public List<NewUserResponse> getAllUsers() {
        return userService.listUsers()
                .stream()
                .map(NewUserResponse::of) // Convertimos cada User en NewUserResponse
                .toList();
    }
    /* Obtener usuario */
    @Operation(summary = "Obtener usuario por ID", description = "Buscar usuario por su identificador")
    @GetMapping("/user/{id}")
    public NewUserResponse getUserById(
            @Parameter(description = "ID del usuario", required = true)
            @PathVariable Long id) {
        return NewUserResponse.of(userService.getUser(id));
    }

    @Operation(summary = "Obtener usuario por Email", description = "Buscar usuario por su correo electrónico")
    @GetMapping("/user/by-email/{email}")
    public NewUserResponse getUserByEmail(
            @Parameter(description = "Email del usuario", required = true)
            @PathVariable String email) {
        return NewUserResponse.of(userService.getUser(email));
    }

    /* Crear usuario */
    @Operation(summary = "Crear usuario", description = "Crea un nuevo usuario enviando un JSON con sus datos")
    @PostMapping("/user")
    public ResponseEntity<NewUserResponse> createUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del nuevo usuario",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = User.class),
                            examples = @ExampleObject(
                                    value = "{ \"username\": \"nuevoUser\", \"password\": \"1234\", \"email\": \"correo@example.com\", \"role\": \"USER\" }"
                            )
                    )
            ) NewUserCommand cmd) {
        User user = userService.register(cmd);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(NewUserResponse.of(user));
    }


    /* Editar usuario */
    @Operation(summary = "Actualizar usuario", description = "Actualizar datos del usuario")
    @PutMapping("/user/{id}")
    public ResponseEntity<NewUserResponse> editUser(
            @Parameter(description = "ID del usuario", required = true)
            @PathVariable Long id,
            @Parameter(description = "Datos actualizados del usuario", required = true)
            @RequestBody EditUserCommand cmd) {

        User user = userService.updatePartialUser(id, cmd);
        return ResponseEntity.ok(NewUserResponse.of(user));
    }

    /* Eliminar usuario */
    @Operation(summary = "Eliminar usuario", description = "Eliminar información de un usuario")
    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> deleteUser(
            @Parameter(description = "IS del usuario a eliminar", required = true)
            @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

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
            @Parameter(description = "ID de la categoria", required = true)
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
                            examples = @ExampleObject(
                                    value = "{ \"title\": \"Nueva Categoría\" }"
                            )
                    )
            ) @org.springframework.web.bind.annotation.RequestBody Category category) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.save(category));
    }

    /* Editar categoria */
    @Operation(summary = "Editar categoria", description = "Editar una categoría existente")
    @PutMapping("/category/{id}")
    public ResponseEntity<Category> editCategory(
            @Parameter(description = "ID de la categoría a editar", required = true)
            @PathVariable Long id,
            @Parameter(description = "Datos modificados de la categoria", required = true)
            @org.springframework.web.bind.annotation.RequestBody EditCategoryCommand category) {
        return ResponseEntity.ok(categoryService.edit(id, category));
    }
    /* Borrar categoria */
    @Operation(summary = "Eliminar categoria", description = "Borrar una categoría por su ID")
    @DeleteMapping("/category/{id}")
    public ResponseEntity<?> deleteCategory(
            @Parameter(description = "ID de la categoría a eliminar", required = true)
            @PathVariable Long id) {
        categoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
