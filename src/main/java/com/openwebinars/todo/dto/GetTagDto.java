package com.openwebinars.todo.dto;

import com.openwebinars.todo.model.Tag;
/* DTO de salida para la obtención de una etiqueta

 Devuelve la información de la etiqueta y del usuario sin contraseña

- No expone info sensible del usuario
*/
public record GetTagDto(
        Long id, String name, NewUserResponse user
) {
    public static GetTagDto of(Tag t) {
        return new GetTagDto(
                t.getId(),
                t.getName(),
                NewUserResponse.of(t.getUser())
        );
    }
}
