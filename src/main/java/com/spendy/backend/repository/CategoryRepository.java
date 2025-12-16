package com.spendy.backend.repository;

import com.spendy.backend.model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends MongoRepository<Category, String> {

    // =========================
    // 🔒 AISLAMIENTO POR USUARIO
    // =========================

    List<Category> findAllByUserID(String userID);

    Optional<Category> findByIdAndUserID(String id, String userID);

    boolean existsByUserIDAndNameIgnoreCase(String userID, String name);
}