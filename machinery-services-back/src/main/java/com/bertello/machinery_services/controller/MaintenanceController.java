package com.bertello.machinery_services.controller;

import com.bertello.machinery_services.dto.request.CreateMaintenanceRequest;
import com.bertello.machinery_services.dto.response.MaintenanceResponse;
import com.bertello.machinery_services.dto.response.PageResponse;
import com.bertello.machinery_services.dto.request.UpdateMaintenanceRequest;
import com.bertello.machinery_services.service.MaintenanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/maintenances")
@RequiredArgsConstructor
@Slf4j
public class MaintenanceController {
    private final MaintenanceService maintenanceService;

    @PostMapping
    public ResponseEntity<MaintenanceResponse> createMaintenance(
            @RequestBody @Valid CreateMaintenanceRequest request) {
        MaintenanceResponse response = maintenanceService.createMaintenance(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/by-customer")
    public ResponseEntity<PageResponse<MaintenanceResponse>> getMaintenancesByCustomerAndDateRange(
            @RequestParam Long customerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate since,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate until,
            @ParameterObject @PageableDefault(size = 10, sort = "maintenanceDate") Pageable pageable) {
        return ResponseEntity.ok(maintenanceService.getMaintenancesByCustomerAndDateRange(
                customerId, since, until, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaintenanceResponse> updateMaintenance(
            @PathVariable Long id,
            @RequestBody @Valid UpdateMaintenanceRequest request) {
        MaintenanceResponse response = maintenanceService.updateMaintenance(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<PageResponse<MaintenanceResponse>> getMaintenancesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate since,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate until,
            @ParameterObject @PageableDefault(size = 10, sort = "maintenanceDate") Pageable pageable) {
        return ResponseEntity.ok(maintenanceService.getMaintenancesByDateRange(since, until, pageable));
    }
}