package com.bertello.machinery_services.service.impl;

import com.bertello.machinery_services.dto.request.CreateMaintenanceRequest;
import com.bertello.machinery_services.dto.response.MaintenanceResponse;
import com.bertello.machinery_services.dto.response.PageResponse;
import com.bertello.machinery_services.dto.request.UpdateMaintenanceRequest;
import com.bertello.machinery_services.entity.CustomerEntity;
import com.bertello.machinery_services.entity.MaintenanceEntity;
import com.bertello.machinery_services.exception.InvalidDateRangeException;
import com.bertello.machinery_services.mapper.MaintenanceMapper;
import com.bertello.machinery_services.repository.CustomerRepository;
import com.bertello.machinery_services.repository.MaintenanceRepository;
import com.bertello.machinery_services.service.MaintenanceService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class MaintenanceServiceImpl implements MaintenanceService {
    private static final ZoneId ZONE = ZoneId.of("America/Argentina/Cordoba");

    private final MaintenanceMapper maintenanceMapper;
    private final MaintenanceRepository maintenanceRepository;
    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public MaintenanceResponse createMaintenance(CreateMaintenanceRequest request) {
        CustomerEntity customerEntity = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

        MaintenanceEntity maintenanceEntity = maintenanceMapper.toEntity(request, customerEntity);
        maintenanceEntity = maintenanceRepository.save(maintenanceEntity);

        refreshCustomerMaintenanceCount(customerEntity);

        return maintenanceMapper.toResponse(maintenanceEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MaintenanceResponse> getMaintenancesByCustomerAndDateRange(Long customerId,
                                                                                   LocalDate since,
                                                                                   LocalDate until,
                                                                                   Pageable pageable) {
        validateSinceBeforeUntil(since, until);

        Page<MaintenanceEntity> page = maintenanceRepository.findByCustomerAndDateRange(
                customerId, toStartOfDay(since), toEndOfDayExclusive(until), pageable);

        return PageResponse.from(page.map(maintenanceMapper::toResponse));
    }

    @Override
    @Transactional
    public MaintenanceResponse updateMaintenance(Long id, UpdateMaintenanceRequest request) {
        MaintenanceEntity maintenanceEntity = maintenanceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Registro de mantenimiento no encontrado"));
        maintenanceEntity.setMaintenanceDate(request.maintenanceDate());
        maintenanceEntity.setInvoiceDate(request.invoiceDate());
        maintenanceEntity.setPlace(request.place());
        maintenanceEntity.setModel(request.model());
        maintenanceEntity.setDescription(request.description());
        MaintenanceEntity saved = maintenanceRepository.save(maintenanceEntity);

        refreshCustomerMaintenanceCount(saved.getCustomer());

        return maintenanceMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MaintenanceResponse> getMaintenancesByDateRange(
            LocalDate since, LocalDate until, Pageable pageable) {
        validateSinceBeforeUntil(since, until);

        if (since.plusMonths(3).isBefore(until)) {
            throw new InvalidDateRangeException("El rango máximo permitido es de 3 meses");
        }

        Page<MaintenanceEntity> pageEntities = maintenanceRepository.findByMaintenanceDateBetween(
                toStartOfDay(since), toEndOfDayExclusive(until), pageable);

        return PageResponse.from(pageEntities.map(maintenanceMapper::toResponse));
    }

    private void refreshCustomerMaintenanceCount(CustomerEntity customer) {
        OffsetDateTime sixMonthsAgo = OffsetDateTime.now().minusMonths(6);
        int count = maintenanceRepository.countByCustomerIdAndMaintenanceDateAfter(customer.getId(), sixMonthsAgo);
        customer.updateMaintenanceCount(count);
    }

    private void validateSinceBeforeUntil(LocalDate since, LocalDate until) {
        if (since.isAfter(until)) {
            throw new InvalidDateRangeException("La fecha 'desde' no puede ser posterior a 'hasta'");
        }
    }

    private OffsetDateTime toStartOfDay(LocalDate date) {
        return date.atStartOfDay(ZONE).toOffsetDateTime();
    }

    private OffsetDateTime toEndOfDayExclusive(LocalDate date) {
        return date.plusDays(1).atStartOfDay(ZONE).toOffsetDateTime();
    }
}