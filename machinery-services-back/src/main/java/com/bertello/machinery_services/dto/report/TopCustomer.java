package com.bertello.machinery_services.dto.report;

import java.time.OffsetDateTime;

public record TopCustomer(
        Long customerId,
        String fullName,
        Long totalMaintenances,
        OffsetDateTime lastMaintenanceDate
){}
