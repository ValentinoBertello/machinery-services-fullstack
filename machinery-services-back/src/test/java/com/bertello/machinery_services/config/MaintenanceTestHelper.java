package com.bertello.machinery_services.config;

import com.bertello.machinery_services.dto.request.CreateMaintenanceRequest;
import com.bertello.machinery_services.entity.CustomerEntity;
import com.bertello.machinery_services.entity.MaintenanceEntity;
import com.bertello.machinery_services.enums.Place;
import com.bertello.machinery_services.repository.MaintenanceReportRepository;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

public final class MaintenanceTestHelper {
    public static MaintenanceEntity createMaintenance(
            CustomerEntity customer,
            String dateString,
            Place place,
            boolean invoiced
    ) {
        OffsetDateTime date = OffsetDateTime.parse(dateString);
        return MaintenanceEntity.builder()
                .maintenanceDate(date)
                .invoiceDate(invoiced ? date.plusDays(1) : null)
                .place(place)
                .model("Tractor X")
                .description("Maintenance test")
                .customer(customer)
                .build();
    }

    public static CreateMaintenanceRequest createMaintenanceRequest (Long customerId, String dateString) {
        Instant instant = Instant.parse(dateString);
        return new CreateMaintenanceRequest(
                instant.atOffset(ZoneOffset.of("-03:00")),
                instant.atOffset(ZoneOffset.of("-03:00")),
                Place.WORKSHOP,
                "Tractor X",
                "Maintenance test",
                customerId
        );
    }

    public static void persistBaseMaintenances(
            MaintenanceReportRepository repository,
            CustomerEntity c1,
            CustomerEntity c2
    ) {
        MaintenanceEntity sinceBoundary = createMaintenance(
                c1, "2026-01-01T00:00:00-03:00", Place.FIELD, false);

        MaintenanceEntity invoicedWorkshop = createMaintenance(
                c1, "2026-01-10T10:00:00-03:00", Place.WORKSHOP, true);

        MaintenanceEntity notInvoicedField = createMaintenance(
                c2, "2026-01-20T15:00:00-03:00", Place.FIELD, false);

        MaintenanceEntity beforeSince = createMaintenance(
                c2, "2025-12-31T23:59:59-03:00", Place.WORKSHOP, false);

        MaintenanceEntity untilBoundary = createMaintenance(
                c2, "2026-02-01T00:00:00-03:00", Place.FIELD, false);

        MaintenanceEntity deleted = createMaintenance(
                c2, "2026-01-15T12:00:00-03:00", Place.WORKSHOP, false);
        deleted.setDeletedAt(OffsetDateTime.parse("2026-01-16T12:00:00-03:00"));

        repository.saveAll(List.of(
                sinceBoundary,
                invoicedWorkshop,
                notInvoicedField,
                beforeSince,
                untilBoundary,
                deleted
        ));
        repository.flush();
    }
}