package com.bertello.machinery_services.controller;

import com.bertello.machinery_services.dto.report.DashboardReportResponse;
import com.bertello.machinery_services.service.MaintenanceReportService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Slf4j
public class MaintenanceReportController {
    private final MaintenanceReportService maintenanceReportService;

    @GetMapping
    public ResponseEntity<DashboardReportResponse> getDashboard(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate since,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate until,
            @RequestParam(defaultValue = "5") @Min(1) @Max(20) int limit) {
        return ResponseEntity.ok(
                maintenanceReportService.getDashboard(since, until, limit));
    }
}
