package com.bertello.machinery_services.dto.report;

import java.util.List;

public record DashboardReportResponse(
        MaintenanceSummary summary,
        List<MonthlyMaintenance> monthlySeries,
        List<TopCustomer> topCustomers
){}
