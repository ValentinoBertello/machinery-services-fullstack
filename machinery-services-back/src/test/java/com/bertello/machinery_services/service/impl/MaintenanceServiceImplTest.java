package com.bertello.machinery_services.service.impl;

import com.bertello.machinery_services.dto.request.CreateMaintenanceRequest;
import com.bertello.machinery_services.dto.response.MaintenanceResponse;
import com.bertello.machinery_services.dto.response.PageResponse;
import com.bertello.machinery_services.dto.request.UpdateMaintenanceRequest;
import com.bertello.machinery_services.entity.CustomerEntity;
import com.bertello.machinery_services.entity.MaintenanceEntity;
import com.bertello.machinery_services.enums.Place;
import com.bertello.machinery_services.exception.InvalidDateRangeException;
import com.bertello.machinery_services.mapper.MaintenanceMapper;
import com.bertello.machinery_services.repository.CustomerRepository;
import com.bertello.machinery_services.repository.MaintenanceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static com.bertello.machinery_services.config.MaintenanceTestHelper.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MaintenanceServiceImplTest {
    @Spy
    private MaintenanceMapper maintenanceMapper;
    @Mock
    private MaintenanceRepository maintenanceRepository;
    @Mock
    private CustomerRepository customerRepository;
    @InjectMocks
    private MaintenanceServiceImpl service;

    CreateMaintenanceRequest maintenanceRequest;
    CustomerEntity c1;
    MaintenanceEntity maintenanceEntity;

    @BeforeEach
    void setup() {
        // createMaintenanceRequest usa Instant.parse, por eso este string lleva "Z"
        maintenanceRequest = createMaintenanceRequest(1L, "2026-10-21T15:00:00Z");

        c1 = CustomerEntity.builder()
                .id(1L)
                .fullName("Agroempresa Santa Rita")
                .maintenanceCount(0)
                .build();

        maintenanceEntity = createMaintenance(c1, "2026-10-21T12:00:00-03:00", Place.WORKSHOP, true);
    }

    @Test
    void createMaintenanceShouldThrowEntityNotFoundException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> service.createMaintenance(maintenanceRequest)
        );

        assertEquals("Cliente no encontrado", ex.getMessage());
    }

    @Test
    void createMaintenanceWithExistingCustomerUpdatesLevel() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(c1));
        when(maintenanceRepository.save(any())).thenReturn(maintenanceEntity);
        when(maintenanceRepository.countByCustomerIdAndMaintenanceDateAfter(eq(1L), any(OffsetDateTime.class)))
                .thenReturn(26);

        MaintenanceResponse result = service.createMaintenance(maintenanceRequest);

        assertNotNull(result);
        assertEquals(26, c1.getMaintenanceCount());
    }

    @Test
    void getMaintenancesByCustomerAndDateRangeTest() {
        LocalDate since = LocalDate.of(2026, 10, 1);
        LocalDate until = LocalDate.of(2026, 10, 31);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("maintenanceDate").descending());

        // since inclusivo a las 00:00, until exclusivo al día siguiente a las 00:00
        OffsetDateTime from = OffsetDateTime.parse("2026-10-01T00:00:00-03:00");
        OffsetDateTime to = OffsetDateTime.parse("2026-11-01T00:00:00-03:00");

        Page<MaintenanceEntity> entityPage = new PageImpl<>(List.of(maintenanceEntity), pageable, 1);

        when(maintenanceRepository.findByCustomerAndDateRange(1L, from, to, pageable))
                .thenReturn(entityPage);

        PageResponse<MaintenanceResponse> result =
                service.getMaintenancesByCustomerAndDateRange(1L, since, until, pageable);

        assertNotNull(result);
    }

    @Test
    void updateMaintenanceTest() {
        UpdateMaintenanceRequest request = new UpdateMaintenanceRequest(
                OffsetDateTime.parse("2026-11-01T10:00:00-03:00"),
                OffsetDateTime.parse("2026-11-01T10:00:00-03:00"),
                Place.WORKSHOP,
                "Tractor Y",
                "Actualizado"
        );

        when(maintenanceRepository.findById(1L)).thenReturn(Optional.of(maintenanceEntity));
        when(maintenanceRepository.save(any())).thenReturn(maintenanceEntity);
        when(maintenanceRepository.countByCustomerIdAndMaintenanceDateAfter(eq(1L), any(OffsetDateTime.class)))
                .thenReturn(1);
        MaintenanceResponse result = service.updateMaintenance(1L, request);

        assertNotNull(result);
        assertEquals(Place.WORKSHOP, maintenanceEntity.getPlace());
        assertEquals("Tractor Y", maintenanceEntity.getModel());
        assertTrue(maintenanceEntity.getMaintenanceDate()
                .isEqual(OffsetDateTime.parse("2026-11-01T10:00:00-03:00")));
    }

    @Test
    void updateMaintenanceThrowsWhenNotFound() {
        when(maintenanceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> service.updateMaintenance(1L, new UpdateMaintenanceRequest(
                        OffsetDateTime.now(), null, Place.WORKSHOP, "Tractor X", "x")));
    }

    @Test
    void getMaintenancesTest() {
        LocalDate since = LocalDate.of(2026, 1, 1);
        LocalDate until = LocalDate.of(2026, 3, 31);
        Pageable pageable = PageRequest.of(0, 10);

        OffsetDateTime from = OffsetDateTime.parse("2026-01-01T00:00:00-03:00");
        OffsetDateTime to = OffsetDateTime.parse("2026-04-01T00:00:00-03:00");

        Page<MaintenanceEntity> page = new PageImpl<>(List.of(maintenanceEntity), pageable, 1);

        when(maintenanceRepository.findByMaintenanceDateBetween(from, to, pageable))
                .thenReturn(page);

        PageResponse<MaintenanceResponse> result = service.getMaintenancesByDateRange(since, until, pageable);

        assertNotNull(result);
    }

    @Test
    void getMaintenancesThrowsWhenSinceAfterUntil() {
        Pageable pageable = PageRequest.of(0, 10);

        assertThrows(InvalidDateRangeException.class,
                () -> service.getMaintenancesByDateRange(LocalDate.of(2026, 5, 1), LocalDate.of(2026, 1, 1), pageable));
    }

    @Test
    void getMaintenancesThrowsWhenRangeExceedsThreeMonths() {
        Pageable pageable = PageRequest.of(0, 10);

        assertThrows(InvalidDateRangeException.class,
                () -> service.getMaintenancesByDateRange(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 5, 1), pageable));
    }
}