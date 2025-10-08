package com.spendy.backend.controller;

import com.spendy.backend.dto.TransactionCreateDTO;
import com.spendy.backend.model.Transaction;
import com.spendy.backend.repository.TransactionRepository;
import com.spendy.backend.repository.CategoryRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    public TransactionController(TransactionRepository transactionRepository,
                                 CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody TransactionCreateDTO dto) {

        // ✅ Validación: verificar que exista la categoría antes de guardar
        if (!categoryRepository.existsById(dto.getCategoryId())) {
            return ResponseEntity.badRequest().body(
                    java.util.Map.of("error", "Category not found")
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
}