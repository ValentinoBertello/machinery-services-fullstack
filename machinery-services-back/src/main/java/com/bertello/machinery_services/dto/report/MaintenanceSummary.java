package com.bertello.machinery_services.dto.report;

import java.math.BigDecimal;

public record MaintenanceSummary(
        Long totalMaintenances,
        Long distinctCustomers,
        Long notInvoicedCount,
        Long inFieldCount,
        Long inWorkshopCount,
        Long periodDays,
        BigDecimal avgMonthlyMaintenances
){}
