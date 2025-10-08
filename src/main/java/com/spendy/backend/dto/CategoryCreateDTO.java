package com.spendy.backend.dto;
import jakarta.validation.constraints.NotBlank;

public class CategoryCreateDTO {
    @NotBlank private String name;
    private String color;
    public String getName(){ return name; }
    public String getColor(){ return color; }
    public void setName(String name){ this.name = name; }
    public void setColor(String color){ this.color = color; }
}