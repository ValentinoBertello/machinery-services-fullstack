package com.bertello.machinery_services.service;

import com.bertello.machinery_services.dto.report.DashboardReportResponse;

import java.time.LocalDate;

public interface MaintenanceReportService {
    DashboardReportResponse getDashboard(LocalDate since, LocalDate until, int limit);


}
