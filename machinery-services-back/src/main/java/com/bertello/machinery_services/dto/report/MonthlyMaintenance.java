package com.bertello.machinery_services.dto.report;

public record MonthlyMaintenance(
        Integer year,
        Integer month,
        Long total
) {
}
