package com.bertello.machinery_services.dto.report;

public record MaintenanceSummaryRaw(
        Long totalMaintenances,
        Long distinctCustomers,
        Long notInvoicedCount,
        Long inFieldCount,
        Long inWorkshopCount
){}
