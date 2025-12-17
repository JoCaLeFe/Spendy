package com.spendy.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Total agrupado por categoría (para reportes)")
public class CategoryTotalDTO {

    @Schema(description = "ID de la categoría", example = "6941ddd868190c4b2a1d64ab")
    public String categoryId;

    @Schema(description = "Nombre de la categoría", example = "Despensa")
    public String name;

    @Schema(description = "Total acumulado para la categoría", example = "469.49")
    public double total;

    public CategoryTotalDTO(String categoryId, String name, double total) {
        this.categoryId = categoryId;
        this.name = name;
        this.total = total;
    }
}