package com.spendy.backend.controller;

import com.spendy.backend.dto.CategoryCreateDTO;
import com.spendy.backend.exception.ResourceNotFoundException;
import com.spendy.backend.model.Category;
import com.spendy.backend.repository.CategoryRepository;
import com.spendy.backend.service.PatchUtils;
import jakarta.validation.Valid;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryRepository repo;
    private final PatchUtils patchUtils;

    public CategoryController(CategoryRepository repo, PatchUtils patchUtils) {
        this.repo = repo;
        this.patchUtils = patchUtils;
    }

    // ✅ Obtener todas las categorías
    @GetMapping
    public List<Category> getAll() {
        return repo.findAll();
    }

    // ✅ Obtener categoría por ID (usa excepción 404 uniforme)
    @GetMapping("/{id}")
    public Category getById(@PathVariable String id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", id));
    }

    // ✅ Crear nueva categoría
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CategoryCreateDTO dto) {
        if (repo.existsByNameIgnoreCase(dto.getName())) {
            return ResponseEntity.badRequest().body(Map.of("error", "La categoría ya existe"));
        }
        Category saved = repo.save(new Category(null, dto.getName(), dto.getColor()));
        return ResponseEntity.created(URI.create("/api/categories/" + saved.getId())).body(saved);
    }

    // ✅ PATCH parcial con JSON-Patch
    @PatchMapping(value = "/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<?> patch(@PathVariable String id,
                                   @RequestBody List<Map<String, Object>> ops) {

        Category current = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", id));

        // Solo permitimos modificar name y color
        var allowed = Set.of("/name", "/color");
        boolean badPath = ops.stream().anyMatch(op -> {
            Object p = op.get("path");
            return !(p instanceof String s) || !allowed.contains(s);
        });
        if (badPath) {
            return ResponseEntity.badRequest().body(Map.of("error", "Ruta de patch inválida"));
        }

        try {
            Category updated = patchUtils.applyPatch(current, ops);

            // Validar nombre
            if (updated.getName() != null) {
                String newName = updated.getName().trim();
                if (newName.isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of("error", "El nombre no puede estar vacío"));
                }
                if (!newName.equalsIgnoreCase(current.getName()) && repo.existsByNameIgnoreCase(newName)) {
                    return ResponseEntity.status(409).body(Map.of("error", "El nombre de la categoría ya existe"));
                }
                updated.setName(newName);
            }

            // Validar color HEX #RRGGBB
            if (updated.getColor() != null) {
                String c = updated.getColor().trim();
                var hex = Pattern.compile("^#([A-Fa-f0-9]{6})$");
                if (!hex.matcher(c).matches()) {
                    return ResponseEntity.badRequest().body(Map.of("error", "El color debe ser HEX como #33CC99"));
                }
                updated.setColor(c);
            }

            return ResponseEntity.ok(repo.save(updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ Búsqueda dinámica con Query By Example
    @GetMapping("/search")
    public List<Category> search(
            @RequestParam Optional<String> name,
            @RequestParam Optional<String> color
    ) {
        Category probe = new Category();
        name.ifPresent(probe::setName);
        color.ifPresent(probe::setColor);

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withMatcher("name", m -> m.contains().ignoreCase())
                .withMatcher("color", m -> m.ignoreCase());

        Example<Category> example = Example.of(probe, matcher);
        return repo.findAll(example);
    }
}