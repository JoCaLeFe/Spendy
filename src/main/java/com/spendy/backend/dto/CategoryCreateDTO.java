package com.spendy.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO para crear una categoría")
public class CategoryCreateDTO {

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 2, max = 40, message = "El nombre debe tener entre 2 y 40 caracteres")
    @Schema(description = "Nombre de la categoría", example = "Despensa")
    private String name;

    @NotBlank(message = "El color es requerido")
    @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "El color debe ser HEX (#RRGGBB)")
    @Schema(description = "Color en formato HEX (#RRGGBB)", example = "#33CC99")
    private String color;

    public String getName() { return name; }
    public String getColor() { return color; }
    public void setName(String name) { this.name = name; }
    public void setColor(String color) { this.color = color; }
}