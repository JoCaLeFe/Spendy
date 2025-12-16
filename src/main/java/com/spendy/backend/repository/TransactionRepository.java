package com.spendy.backend.repository;

import com.spendy.backend.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends MongoRepository<Transaction, String> {

    // =========================
    // 🔒 AISLAMIENTO POR USUARIO
    // =========================

    List<Transaction> findByUserIDAndDateBetween(
            String userID,
            LocalDate start,
            LocalDate end
    );

    Optional<Transaction> findByIdAndUserID(String id, String userID);

    // =========================
    // 📄 CURSOR PAGINATION
    // =========================

    // Primera "página"
    List<Transaction> findByUserIDOrderByCreatedAtDesc(String userID);

    // Páginas siguientes
    List<Transaction> findByUserIDAndCreatedAtBeforeOrderByCreatedAtDesc(
            String userID,
            Instant cursor
    );
}