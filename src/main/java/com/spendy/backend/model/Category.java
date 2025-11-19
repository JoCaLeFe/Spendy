package com.spendy.backend.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "categories")
@JsonInclude(JsonInclude.Include.NON_NULL)        // Ocultar campos null
public class Category {

    // Vistas de serialización
    public interface ViewList {}
    public interface ViewDetail extends ViewList {}

    @Id
    @JsonView(ViewList.class)
    private String id;

    @JsonView(ViewList.class)
    private String name;

    @JsonView(ViewDetail.class)
    private String color;

    public Category() {}
    public Category(String id, String name, String color) {
        this.id = id; this.name = name; this.color = color;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getColor() { return color; }
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setColor(String color) { this.color = color; }
}