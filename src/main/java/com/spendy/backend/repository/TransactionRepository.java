package com.spendy.backend.repository;

import com.spendy.backend.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends MongoRepository<Transaction, String> {

    List<Transaction> findByDateBetween(LocalDate start, LocalDate end);

    // 👉 Para la primera "página" por cursor: ordenado por createdAt DESC
    List<Transaction> findAllByOrderByCreatedAtDesc();

    // 👉 Para las siguientes páginas: elementos con createdAt < cursor, también ordenado DESC
    List<Transaction> findByCreatedAtBeforeOrderByCreatedAtDesc(Instant cursor);
}