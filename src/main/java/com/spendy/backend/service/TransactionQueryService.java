package com.spendy.backend.service;

import com.spendy.backend.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class TransactionQueryService {

    private final MongoTemplate mongo;

    public TransactionQueryService(MongoTemplate mongo) {
        this.mongo = mongo;
    }

    public Page<Transaction> search(
            Optional<LocalDate> from,
            Optional<LocalDate> to,
            Optional<String> categoryId,
            Optional<String> method,
            Optional<String> type,
            Optional<Double> minAmount,
            Optional<Double> maxAmount,
            Optional<String> q,
            Pageable pageable
    ) {
        List<Criteria> conditions = new ArrayList<>();

        // Rango de fechas (en una sola Criteria sobre "date")
        LocalDate f = from.orElse(null);
        LocalDate t = to.orElse(null);
        if (f != null || t != null) {
            Criteria byDate = Criteria.where("date");
            if (f != null) byDate = byDate.gte(f);
            if (t != null) byDate = byDate.lte(t);
            conditions.add(byDate);
        }

        // Rango de importe (una sola Criteria sobre "amount")
        Double min = minAmount.orElse(null);
        Double max = maxAmount.orElse(null);
        if (min != null || max != null) {
            Criteria byAmount = Criteria.where("amount");
            if (min != null) byAmount = byAmount.gte(min);
            if (max != null) byAmount = byAmount.lte(max);
            conditions.add(byAmount);
        }

        categoryId.ifPresent(c -> conditions.add(Criteria.where("categoryId").is(c)));
        method.ifPresent(m -> conditions.add(Criteria.where("method").is(m.toUpperCase())));
        type.ifPresent(tp -> conditions.add(Criteria.where("type").is(tp.toUpperCase())));
        q.ifPresent(text ->
                conditions.add(Criteria.where("note").regex(".*" + Pattern.quote(text) + ".*", "i"))
        );

        Query query = new Query();
        if (!conditions.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(conditions.toArray(new Criteria[0])));
        }

        long total = mongo.count(query, Transaction.class);
        query.with(pageable);

        List<Transaction> content = mongo.find(query, Transaction.class);
        return new PageImpl<>(content, pageable, total);
    }
}
