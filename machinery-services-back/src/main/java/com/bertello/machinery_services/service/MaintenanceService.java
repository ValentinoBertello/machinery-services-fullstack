package com.bertello.machinery_services.service;

import com.bertello.machinery_services.dto.request.CreateMaintenanceRequest;
import com.bertello.machinery_services.dto.response.MaintenanceResponse;
import com.bertello.machinery_services.dto.response.PageResponse;
import com.bertello.machinery_services.dto.request.UpdateMaintenanceRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public interface MaintenanceService {
    MaintenanceResponse createMaintenance(CreateMaintenanceRequest request);

    PageResponse<MaintenanceResponse> getMaintenancesByCustomerAndDateRange(Long customerId, LocalDate since,
                                                                                  LocalDate until, Pageable pageable);

    MaintenanceResponse updateMaintenance(Long id, UpdateMaintenanceRequest request);

    PageResponse<MaintenanceResponse> getMaintenancesByDateRange(LocalDate since, LocalDate until, Pageable pageable);
}
