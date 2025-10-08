package com.spendy.backend.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="categories")
public class Category {
    @Id private String id;
    private String name;
    private String color;
    public Category() {}
    public Category(String id,String name,String color){this.id=id;this.name=name;this.color=color;}
    public String getId(){return id;} public String getName(){return name;} public String getColor(){return color;}
    public void setId(String id){this.id=id;} public void setName(String name){this.name=name;} public void setColor(String color){this.color=color;}
}