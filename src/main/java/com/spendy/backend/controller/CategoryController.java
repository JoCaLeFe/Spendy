package com.spendy.backend.controller;

import com.spendy.backend.dto.CategoryCreateDTO;
import com.spendy.backend.model.Category;
import com.spendy.backend.repository.CategoryRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryRepository repo;
    public CategoryController(CategoryRepository repo){ this.repo = repo; }

    @GetMapping
    public List<Category> getAll(){ return repo.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getById(@PathVariable String id){
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CategoryCreateDTO dto){
        if (repo.existsByNameIgnoreCase(dto.getName()))
            return ResponseEntity.badRequest().body(java.util.Map.of("error","Category already exists"));
        Category saved = repo.save(new Category(null, dto.getName(), dto.getColor()));
        return ResponseEntity.created(URI.create("/api/categories/"+saved.getId())).body(saved);
    }
}