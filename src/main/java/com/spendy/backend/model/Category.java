package com.spendy.backend.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Document(collection = "categories")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Category {

    public interface ViewList {}
    public interface ViewDetail extends ViewList {}

    @Id
    @JsonView(ViewList.class)
    private String id;

    // 👇 NUEVO (NO SE SERIALIZA)
    @JsonIgnore
    private String userID;

    @JsonView(ViewList.class)
    private String name;

    @JsonView(ViewDetail.class)
    private String color;

    public Category() {}

    // Constructor actual
    public Category(String id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    // 👇 NUEVO constructor con userID
    public Category(String id, String userID, String name, String color) {
        this(id, name, color);
        this.userID = userID;
    }

    public String getId() { return id; }
    public String getUserID() { return userID; }
    public String getName() { return name; }
    public String getColor() { return color; }

    public void setId(String id) { this.id = id; }
    public void setUserID(String userID) { this.userID = userID; }
    public void setName(String name) { this.name = name; }
    public void setColor(String color) { this.color = color; }
}