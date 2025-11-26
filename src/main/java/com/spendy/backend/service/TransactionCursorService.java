package com.spendy.backend.service;

import com.spendy.backend.model.Transaction;
import com.spendy.backend.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TransactionCursorService {

    private final TransactionRepository repo;

    public TransactionCursorService(TransactionRepository repo) {
        this.repo = repo;
    }

    /**
     * Cursor-based pagination:
     * - Si cursor == null → devuelve las primeras `limit` transacciones.
     * - Si cursor != null → devuelve transacciones antes del cursor.
     */
    public Map<String, Object> getWithCursor(Instant cursor, int limit) {

        // 1️⃣ Obtener resultados según si viene cursor o no
        List<Transaction> data = (cursor == null)
                ? repo.findAllByOrderByCreatedAtDesc()
                : repo.findByCreatedAtBeforeOrderByCreatedAtDesc(cursor);

        // 2️⃣ Recortar a `limit`
        if (data.size() > limit) {
            data = data.subList(0, limit);
        }

        // 3️⃣ Calcular nextCursor
        Instant nextCursor = data.isEmpty()
                ? null
                : data.get(data.size() - 1).getCreatedAt();

        // 4️⃣ Respuesta
        Map<String, Object> response = new HashMap<>();
        response.put("data", data);
        response.put("nextCursor", nextCursor);

        return response;
    }
}