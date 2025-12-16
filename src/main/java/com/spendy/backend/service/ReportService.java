package com.spendy.backend.service;

import com.spendy.backend.dto.CategoryTotalDTO;
import com.spendy.backend.dto.MonthlyReportDTO;
import com.spendy.backend.model.Transaction;
import com.spendy.backend.model.Category;
import com.spendy.backend.repository.TransactionRepository;
import com.spendy.backend.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

import static com.spendy.backend.security.util.SecurityUtils.currentUserID;

@Service
public class ReportService {
    private final TransactionRepository txRepo;
    private final CategoryRepository catRepo;

    public ReportService(TransactionRepository txRepo, CategoryRepository catRepo) {
        this.txRepo = txRepo;
        this.catRepo = catRepo;
    }

    public MonthlyReportDTO monthly(int year, int month) {
        String userID = currentUserID();

        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        // ✅ Solo transacciones del usuario
        List<Transaction> txs = txRepo.findByUserIDAndDateBetween(userID, start, end);

        double income = txs.stream().filter(t -> "INCOME".equalsIgnoreCase(t.getType()))
                .mapToDouble(Transaction::getAmount).sum();
        double expense = txs.stream().filter(t -> "EXPENSE".equalsIgnoreCase(t.getType()))
                .mapToDouble(Transaction::getAmount).sum();
        double balance = income - expense;

        Map<String, Double> byMethod = txs.stream()
                .filter(t -> t.getMethod() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getMethod().toUpperCase(Locale.ROOT),
                        Collectors.summingDouble(Transaction::getAmount)));

        // ✅ mapa categoryId -> name (solo categorías del usuario)
        Map<String, String> catNames = catRepo.findAllByUserID(userID).stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));

        Map<String, Double> byCat = txs.stream()
                .filter(t -> t.getCategoryId() != null)
                .collect(Collectors.groupingBy(Transaction::getCategoryId,
                        Collectors.summingDouble(Transaction::getAmount)));

        List<CategoryTotalDTO> byCategory = byCat.entrySet().stream()
                .map(e -> new CategoryTotalDTO(
                        e.getKey(),
                        catNames.getOrDefault(e.getKey(), "(sin nombre)"),
                        e.getValue()
                ))
                .sorted(Comparator.comparingDouble((CategoryTotalDTO c) -> c.total).reversed())
                .toList();

        return new MonthlyReportDTO(year, month, income, expense, balance, byMethod, byCategory);
    }
}