package com.bertello.machinery_services.dto.response;

import java.time.OffsetDateTime;

public record CustomerResponse(
        Long id,
        String fullName,
        int maintenanceCount,
        OffsetDateTime createdAt
) {}
