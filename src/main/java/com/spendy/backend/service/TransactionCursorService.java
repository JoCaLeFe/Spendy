package com.spendy.backend.service;

import com.spendy.backend.model.Transaction;
import com.spendy.backend.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.spendy.backend.security.util.SecurityUtils.currentUserID;

@Service
public class TransactionCursorService {

    private final TransactionRepository repo;

    public TransactionCursorService(TransactionRepository repo) {
        this.repo = repo;
    }

    /**
     * Cursor-based pagination (por usuario):
     * - Si cursor == null → devuelve las primeras `limit` transacciones del usuario.
     * - Si cursor != null → devuelve transacciones del usuario antes del cursor.
     */
    public Map<String, Object> getWithCursor(Instant cursor, int limit) {

        String userID = currentUserID();

        // 1️⃣ Obtener resultados según si viene cursor o no
        List<Transaction> data = (cursor == null)
                ? repo.findByUserIDOrderByCreatedAtDesc(userID)
                : repo.findByUserIDAndCreatedAtBeforeOrderByCreatedAtDesc(userID, cursor);

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