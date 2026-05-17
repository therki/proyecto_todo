package com.openwebinars.todo.controller;

import com.openwebinars.todo.dto.NewUserCommand;
import com.openwebinars.todo.dto.NewUserResponse;
import com.openwebinars.todo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Público", description = "Endpoints públicos sin autenticación")
@RequiredArgsConstructor
public class PublicController {

    private final UserService userService;

    @Operation(
            summary = "Registro de nuevo usuario",
            description = "Permite registrar un nuevo usuario en el sistema. No requiere autenticación."
    )
    @PostMapping("/register")
    public ResponseEntity<NewUserResponse> createUser(
            @Parameter(description = "Datos del nuevo usuario", required = true)
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del nuevo usuario",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = NewUserCommand.class),
                            examples = @ExampleObject(
                                    name = "Ejemplo de registro",
                                    value = "{ \"username\": \"nuevoUsuario\", \"fullname\": \"Nombre Completo\", \"email\": \"usuario@example.com\", \"password\": \"12345\" }"
                            )
                    )
            )
            @RequestBody NewUserCommand cmd) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(NewUserResponse.of(userService.register(cmd)));
    }
}