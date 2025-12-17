package com.spendy.backend.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.spendy.backend.dto.CategoryCreateDTO;
import com.spendy.backend.exception.ResourceNotFoundException;
import com.spendy.backend.model.Category;
import com.spendy.backend.repository.CategoryRepository;
import com.spendy.backend.service.PatchUtils;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.spendy.backend.configuration.ApiPaths;

import java.net.URI;
import java.util.*;
import java.util.regex.Pattern;

import io.swagger.v3.oas.annotations.tags.Tag;

import static com.spendy.backend.security.util.SecurityUtils.currentUserID;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Categories", description = "Gestión de categorías del usuario")
@RestController
@RequestMapping(ApiPaths.V1 + "/categories")
public class CategoryController {

    private final CategoryRepository repo;
    private final PatchUtils patchUtils;

    public CategoryController(CategoryRepository repo, PatchUtils patchUtils) {
        this.repo = repo;
        this.patchUtils = patchUtils;
    }

    // ✅ Obtener todas las categorías (solo del usuario)
    @GetMapping
    @JsonView(Category.ViewList.class)
    public List<Category> getAll() {
        String userID = currentUserID();
        return repo.findAllByUserID(userID);
    }

    // ✅ Obtener categoría por ID (solo del usuario)
    @GetMapping("/{id}")
    @JsonView(Category.ViewDetail.class)
    public Category getById(@PathVariable String id) {
        String userID = currentUserID();
        return repo.findByIdAndUserID(id, userID)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", id));
    }

    // ✅ Crear nueva categoría (solo para el usuario)
    @PostMapping
    @JsonView(Category.ViewDetail.class)
    public ResponseEntity<?> create(@Valid @RequestBody CategoryCreateDTO dto) {
        String userID = currentUserID();

        if (repo.existsByUserIDAndNameIgnoreCase(userID, dto.getName())) {
            return ResponseEntity.badRequest().body(Map.of("error", "La categoría ya existe"));
        }

        Category saved = repo.save(new Category(null, userID, dto.getName(), dto.getColor()));

        URI location = URI.create(ApiPaths.V1 + "/categories/" + saved.getId());

        return ResponseEntity.created(location).body(saved);
    }

    // ✅ PATCH parcial con JSON-Patch (solo del usuario)
    @PatchMapping(value = "/{id}", consumes = "application/json-patch+json")
    @JsonView(Category.ViewDetail.class)
    public ResponseEntity<?> patch(@PathVariable String id,
                                   @RequestBody List<Map<String, Object>> ops) {

        String userID = currentUserID();

        Category current = repo.findByIdAndUserID(id, userID)
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

                if (!newName.equalsIgnoreCase(current.getName())
                        && repo.existsByUserIDAndNameIgnoreCase(userID, newName)) {
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

            // Asegurar que no se pierda el userID
            updated.setUserID(userID);

            return ResponseEntity.ok(repo.save(updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ Búsqueda (por nombre/color) solo del usuario
    @GetMapping("/search")
    @JsonView(Category.ViewList.class)
    public List<Category> search(
            @RequestParam Optional<String> name,
            @RequestParam Optional<String> color
    ) {
        String userID = currentUserID();

        List<Category> all = repo.findAllByUserID(userID);

        return all.stream()
                .filter(c -> name.map(n -> c.getName() != null && c.getName().toLowerCase().contains(n.toLowerCase())).orElse(true))
                .filter(c -> color.map(col -> c.getColor() != null && c.getColor().equalsIgnoreCase(col)).orElse(true))
                .toList();
    }
}