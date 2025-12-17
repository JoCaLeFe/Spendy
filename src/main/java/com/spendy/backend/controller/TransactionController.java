package com.spendy.backend.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.spendy.backend.dto.TransactionCreateDTO;
import com.spendy.backend.exception.ResourceNotFoundException;
import com.spendy.backend.model.Transaction;
import com.spendy.backend.repository.CategoryRepository;
import com.spendy.backend.repository.TransactionRepository;
import com.spendy.backend.service.PatchUtils;
import com.spendy.backend.service.TransactionQueryService;
import com.spendy.backend.service.TransactionCursorService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.spendy.backend.configuration.ApiPaths;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import io.swagger.v3.oas.annotations.tags.Tag;

import static com.spendy.backend.security.util.SecurityUtils.currentUserID;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Transactions", description = "Gestión de transacciones (ingresos y gastos)")
@RestController
@RequestMapping(ApiPaths.V1 + "/transactions")
public class TransactionController {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final PatchUtils patchUtils;
    private final TransactionQueryService queryService;
    private final TransactionCursorService cursorService;

    public TransactionController(TransactionRepository transactionRepository,
                                 CategoryRepository categoryRepository,
                                 PatchUtils patchUtils,
                                 TransactionQueryService queryService,
                                 TransactionCursorService cursorService) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.patchUtils = patchUtils;
        this.queryService = queryService;
        this.cursorService = cursorService;
    }

    // 🔎 GET con filtros + paginación (solo del usuario)
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
        String userID = currentUserID();
        return queryService.search(
                userID, from, to, categoryId, method, type, minAmount, maxAmount, q, pageable
        );
    }

    // ✅ paginación por cursor (ya filtra por userID desde el service)
    @GetMapping("/cursor")
    @JsonView(Transaction.ViewList.class)
    public Map<String, Object> getWithCursor(
            @RequestParam(required = false) Instant cursor,
            @RequestParam(defaultValue = "20") int limit
    ) {
        return cursorService.getWithCursor(cursor, limit);
    }

    // 🔹 GET por id (solo del usuario)
    @GetMapping("/{id}")
    @JsonView(Transaction.ViewDetail.class)
    public Transaction getById(@PathVariable String id) {
        String userID = currentUserID();
        return transactionRepository.findByIdAndUserID(id, userID)
                .orElseThrow(() -> new ResourceNotFoundException("Transacción", id));
    }

    // 🟢 POST: crear transacción (solo para el usuario)
    @PostMapping
    @JsonView(Transaction.ViewDetail.class)
    public ResponseEntity<?> create(@Valid @RequestBody TransactionCreateDTO dto) {
        String userID = currentUserID();

        // Validar que la categoría exista y sea del usuario
        if (dto.getCategoryId() != null &&
                categoryRepository.findByIdAndUserID(dto.getCategoryId(), userID).isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "La categoría no existe"));
        }

        Transaction transaction = new Transaction(
                null,
                userID,              // 👈 NUEVO
                dto.getType(),
                dto.getAmount(),
                dto.getCurrency(),
                dto.getCategoryId(),
                dto.getMethod(),
                dto.getDate(),
                dto.getNote(),
                Instant.now()
        );

        Transaction saved = transactionRepository.save(transaction);

        URI location = URI.create(
                ApiPaths.V1 + "/transactions/" + saved.getId()
        );

        return ResponseEntity.created(location).body(saved);
    }

    // 🟣 PATCH: actualización parcial con JSON-Patch (solo del usuario)
    @PatchMapping(value = "/{id}", consumes = "application/json-patch+json")
    @JsonView(Transaction.ViewDetail.class)
    public ResponseEntity<?> patchTransaction(
            @PathVariable String id,
            @RequestBody List<Map<String, Object>> ops) {

        String userID = currentUserID();

        Transaction current = transactionRepository.findByIdAndUserID(id, userID)
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

            // Validar que la categoría exista y sea del usuario
            if (patched.getCategoryId() != null &&
                    categoryRepository.findByIdAndUserID(patched.getCategoryId(), userID).isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "La categoría no existe"));
            }

            // Asegurar ownership
            patched.setUserID(userID);

            return ResponseEntity.ok(transactionRepository.save(patched));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}