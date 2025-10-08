package com.spendy.backend.repository;

import com.spendy.backend.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends MongoRepository<Transaction, String> {
    List<Transaction> findByDateBetween(LocalDate start, LocalDate end);
}
