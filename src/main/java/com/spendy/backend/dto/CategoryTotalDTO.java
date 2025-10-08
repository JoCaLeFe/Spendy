package com.spendy.backend.dto;

public class CategoryTotalDTO {
    public String categoryId;
    public String name;
    public double total;

    public CategoryTotalDTO(String categoryId, String name, double total) {
        this.categoryId = categoryId; this.name = name; this.total = total;
    }
}