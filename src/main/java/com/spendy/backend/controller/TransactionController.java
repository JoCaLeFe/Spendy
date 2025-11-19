package com.spendy.backend.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.spendy.backend.dto.TransactionCreateDTO;
import com.spendy.backend.exception.ResourceNotFoundException;
import com.spendy.backend.model.Transaction;
import com.spendy.backend.repository.CategoryRepository;
import com.spendy.backend.repository.TransactionRepository;
import com.spendy.backend.service.PatchUtils;
import com.spendy.backend.service.TransactionQueryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final PatchUtils patchUtils;
    private final TransactionQueryService queryService;

    public TransactionController(TransactionRepository transactionRepository,
                                 CategoryRepository categoryRepository,
                                 PatchUtils patchUtils,
                                 TransactionQueryService queryService) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.patchUtils = patchUtils;
        this.queryService = queryService;
    }

    // 🔎 GET con filtros + paginación (vista resumida)
    @GetMapping
    public Page<Transaction> search(
            @RequestParam Optional<LocalDate> from,
            @RequestParam Optional<LocalDate> to,
            @RequestParam Optional<String> categoryId,
            @RequestParam Optional<String> method,
            @RequestParam Optional<String> type,
            @RequestParam Optional<Double> minAmount,
            @RequestParam Optional<Double> maxAmount,
            @RequestParam Optional<String> q,
            Pageable pageable
    ) {
        return queryService.search(
                from, to, categoryId, method, type, minAmount, maxAmount, q, pageable
        );
    }

    // 🔹 GET por id (vista detalle)
    @GetMapping("/{id}")
    @JsonView(Transaction.ViewDetail.class)
    public Transaction getById(@PathVariable String id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transacción", id));
    }

    // 🟢 POST: crear transacción (vista detalle)
    @PostMapping
    @JsonView(Transaction.ViewDetail.class)
    public ResponseEntity<?> create(@Valid @RequestBody TransactionCreateDTO dto) {
        if (!categoryRepository.existsById(dto.getCategoryId())) {
            return ResponseEntity.badRequest().body(Map.of("error", "La categoría no existe"));
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
        return ResponseEntity
                .created(URI.create("/api/transactions/" + saved.getId()))
                .body(saved);
    }

    // 🟣 PATCH: actualización parcial con JSON-Patch (vista detalle)
    @PatchMapping(value = "/{id}", consumes = "application/json-patch+json")
    @JsonView(Transaction.ViewDetail.class)
    public ResponseEntity<?> patchTransaction(
            @PathVariable String id,
            @RequestBody List<Map<String, Object>> ops) {

        Transaction current = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transacción", id));

        // Solo permitimos modificar estos campos
        var allowed = Set.of("/note", "/method", "/date", "/amount", "/categoryId");
        boolean badPath = ops.stream().anyMatch(op -> {
            Object p = op.get("path");
            return !(p instanceof String s) || !allowed.contains(s);
        });
        if (badPath) {
            return ResponseEntity.badRequest().body(Map.of("error", "Ruta de patch inválida"));
        }

        try {
            Transaction patched = patchUtils.applyPatch(current, ops);

            if (patched.getAmount() <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "El monto debe ser mayor que 0"));
            }

            if (patched.getCategoryId() != null &&
                    !categoryRepository.existsById(patched.getCategoryId())) {
                return ResponseEntity.badRequest().body(Map.of("error", "La categoría no existe"));
            }

            return ResponseEntity.ok(transactionRepository.save(patched));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}