package com.bertello.machinery_services.dto.response;

import com.bertello.machinery_services.enums.Place;

import java.time.OffsetDateTime;

public record MaintenanceResponse(
        Long id,
        OffsetDateTime maintenanceDate,
        OffsetDateTime invoiceDate,
        Place place,
        String model,
        String description,
        Long customerId,
        String customerName,
        OffsetDateTime createdAt
) {}
