package com.spendy.backend.controller;

import com.spendy.backend.dto.TransactionCreateDTO;
import com.spendy.backend.model.Transaction;
import com.spendy.backend.repository.TransactionRepository;
import com.spendy.backend.repository.CategoryRepository;
import com.spendy.backend.service.PatchUtils;   // 👈 importa tu PatchUtils
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final PatchUtils patchUtils; // 👈 nueva dependencia

    // 👇 incluye patchUtils en el constructor
    public TransactionController(TransactionRepository transactionRepository,
                                 CategoryRepository categoryRepository,
                                 PatchUtils patchUtils) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.patchUtils = patchUtils;
    }

    // 🟢 POST: crear transacción
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody TransactionCreateDTO dto) {

        if (!categoryRepository.existsById(dto.getCategoryId())) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Category not found")
            );
        }

        Transaction transaction = new Transaction(
                null,
                dto.getType(),
                dto.getAmount(),
                dto.getCurrency(),
                dto.getCategoryId(),
                dto.getMethod(),
                dto.getDate(),
                dto.getNote()
        );

        Transaction saved = transactionRepository.save(transaction);
        return ResponseEntity.created(URI.create("/api/transactions/" + saved.getId())).body(saved);
    }

    // 🟣 PATCH: actualización parcial con JSON-Patch
    @PatchMapping(value = "/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<?> patchTransaction(
            @PathVariable String id,
            @RequestBody List<Map<String, Object>> ops) {

        var currentOpt = transactionRepository.findById(id);
        if (currentOpt.isEmpty()) return ResponseEntity.notFound().build();

        // ✅ Solo se permiten estos campos a modificar
        var allowed = Set.of("/note", "/method", "/date", "/amount", "/categoryId");
        boolean badPath = ops.stream().anyMatch(op -> {
            Object p = op.get("path");
            return !(p instanceof String s) || !allowed.contains(s);
        });
        if (badPath)
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid patch path"));

        try {
            Transaction current = currentOpt.get();
            Transaction patched = patchUtils.applyPatch(current, ops);

            // Validaciones de negocio simples
            if (patched.getAmount() <= 0)
                return ResponseEntity.badRequest().body(Map.of("error", "Amount must be > 0"));

            if (patched.getCategoryId() != null &&
                    !categoryRepository.existsById(patched.getCategoryId()))
                return ResponseEntity.badRequest().body(Map.of("error", "Category not found"));

            return ResponseEntity.ok(transactionRepository.save(patched));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}