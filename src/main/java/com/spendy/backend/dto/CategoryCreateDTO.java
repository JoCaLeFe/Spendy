package com.spendy.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CategoryCreateDTO {

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 2, max = 40, message = "El nombre debe tener entre 2 y 40 caracteres")
    private String name;

    @NotBlank(message = "El color es requerido")
    @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "El color debe ser HEX (#RRGGBB)")
    private String color;

    public String getName() { return name; }
    public String getColor() { return color; }
    public void setName(String name) { this.name = name; }
    public void setColor(String color) { this.color = color; }
}