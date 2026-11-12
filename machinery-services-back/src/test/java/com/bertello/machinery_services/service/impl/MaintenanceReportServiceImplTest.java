package com.bertello.machinery_services.service.impl;

import com.bertello.machinery_services.dto.report.*;
import com.bertello.machinery_services.enums.Place;
import com.bertello.machinery_services.exception.InvalidDateRangeException;
import com.bertello.machinery_services.repository.MaintenanceReportRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaintenanceReportServiceImplTest {
    @Mock
    private MaintenanceReportRepository maintenanceReportRepository;

    @InjectMocks
    private MaintenanceReportServiceImpl maintenanceReportService;

    @Test
    void getDashboard_composesSectionsAndTranslatesDates() {
        LocalDate since = LocalDate.of(2026, 1, 1);
        LocalDate until = LocalDate.of(2026, 1, 31);

        MaintenanceSummaryRaw summary = new MaintenanceSummaryRaw(10L, 3L, 2L, 6L, 4L);
        MonthlyMaintenance january = new MonthlyMaintenance(2026, 1, 5L);
        List<TopCustomer> top = List.of(mock(TopCustomer.class));

        when(maintenanceReportRepository.getMaintenanceSummary(any(), any(), any(), any()))
                .thenReturn(summary);
        when(maintenanceReportRepository.getMonthlyCounts(any(), any()))
                .thenReturn(List.of(january));
        when(maintenanceReportRepository.getTopCustomers(any(), any(), any()))
                .thenReturn(top);

        DashboardReportResponse result = maintenanceReportService.getDashboard(since, until, 7);

        OffsetDateTime expectedSince = OffsetDateTime.parse("2026-01-01T00:00:00-03:00");
        OffsetDateTime expectedUntil = OffsetDateTime.parse("2026-02-01T00:00:00-03:00");

        verify(maintenanceReportRepository).getMaintenanceSummary(
                expectedSince, expectedUntil, Place.FIELD, Place.WORKSHOP);
        verify(maintenanceReportRepository).getTopCustomers(
                expectedSince, expectedUntil, PageRequest.of(0, 7));
    }

    @Test
    void monthlySeries_fillsMissingMonthsWithZero_acrossYearBoundary() {
        LocalDate since = LocalDate.of(2025, 11, 1);
        LocalDate until = LocalDate.of(2026, 2, 28);

        when(maintenanceReportRepository.getMaintenanceSummary(any(), any(), any(), any()))
                .thenReturn(new MaintenanceSummaryRaw(0L, 0L, 0L, 0L, 0L));
        when(maintenanceReportRepository.getTopCustomers(any(), any(), any()))
                .thenReturn(List.of());

        when(maintenanceReportRepository.getMonthlyCounts(any(), any()))
                .thenReturn(List.of(
                        new MonthlyMaintenance(2025, 12, 4L),
                        new MonthlyMaintenance(2026, 2, 1L))
                );

        List<MonthlyMaintenance> result =
                maintenanceReportService.getDashboard(since, until, 5).monthlySeries();

        assertThat(result).containsExactly(
                new MonthlyMaintenance(2025, 11, 0L),
                new MonthlyMaintenance(2025, 12, 4L),
                new MonthlyMaintenance(2026, 1, 0L),
                new MonthlyMaintenance(2026, 2, 1L)
        );
    }

    @Test
    void rejectsRangeLongerThan24Months() {
        LocalDate since = LocalDate.of(2024, 1, 1);
        LocalDate until = LocalDate.of(2026, 1, 2); // 24 meses + 1 día

        assertThatThrownBy(() -> maintenanceReportService.getDashboard(since, until, 5))
                .isInstanceOf(InvalidDateRangeException.class)
                .hasMessageContaining("24");

        verifyNoInteractions(maintenanceReportRepository);
    }

    @Test
    void getSummary_computesInclusivePeriodDaysAndAverageMonthly() {
        LocalDate since = LocalDate.of(2026, 1, 1);
        LocalDate until = LocalDate.of(2026, 1, 31);

        MaintenanceSummaryRaw summary = new MaintenanceSummaryRaw(10L, 3L, 2L, 6L, 4L);

        when(maintenanceReportRepository.getMaintenanceSummary(any(), any(), any(), any()))
                .thenReturn(summary);
        when(maintenanceReportRepository.getMonthlyCounts(any(), any()))
                .thenReturn(List.of());
        when(maintenanceReportRepository.getTopCustomers(any(), any(), any()))
                .thenReturn(List.of());

        DashboardReportResponse result = maintenanceReportService.getDashboard(since, until, 5);

        assertThat(result.summary().periodDays()).isEqualTo(31);
        assertThat(result.summary().avgMonthlyMaintenances())
                .isEqualByComparingTo("9.68");
    }
}