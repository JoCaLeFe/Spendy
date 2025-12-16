package com.spendy.backend.controller;

import com.spendy.backend.dto.MonthlyReportDTO;
import com.spendy.backend.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.spendy.backend.configuration.ApiPaths;

@RestController
@RequestMapping(ApiPaths.V1 + "/reports")
public class ReportController {
    private final ReportService service;

    public ReportController(ReportService service) { this.service = service; }

    // GET /api/reports/monthly?year=2025&month=10
    @GetMapping("/monthly")
    public ResponseEntity<MonthlyReportDTO> monthly(
            @RequestParam int year,
            @RequestParam int month) {
        if (month < 1 || month > 12) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(service.monthly(year, month));
    }
}