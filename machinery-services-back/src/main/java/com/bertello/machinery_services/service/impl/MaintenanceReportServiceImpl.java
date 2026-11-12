package com.bertello.machinery_services.service.impl;

import com.bertello.machinery_services.dto.report.*;
import com.bertello.machinery_services.enums.Place;
import com.bertello.machinery_services.exception.InvalidDateRangeException;
import com.bertello.machinery_services.repository.MaintenanceReportRepository;
import com.bertello.machinery_services.service.MaintenanceReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaintenanceReportServiceImpl implements MaintenanceReportService {
    private static final ZoneId ZONE = ZoneId.of("America/Argentina/Cordoba");
    private static final int MAX_MONTHS = 24;

    private final MaintenanceReportRepository maintenanceReportRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardReportResponse getDashboard(LocalDate since, LocalDate until, int limit) {
        validateRange(since, until);
        return new DashboardReportResponse(
                getSummary(since, until),
                getMonthlySeries(since, until),
                getTopCustomers(since, until, limit)
        );
    }

    private MaintenanceSummary getSummary(LocalDate since, LocalDate until) {
        MaintenanceSummaryRaw counts = maintenanceReportRepository.getMaintenanceSummary(
                toStartOfDay(since),
                toEndOfDayExclusive(until),
                Place.FIELD,
                Place.WORKSHOP
        );

        long periodDays = ChronoUnit.DAYS.between(since, until) + 1;
        BigDecimal averageMonthlyMaintenances = computeAvgMonthly(counts.totalMaintenances(), periodDays);

        return new MaintenanceSummary(
                counts.totalMaintenances(),
                counts.distinctCustomers(),
                counts.notInvoicedCount(),
                counts.inFieldCount(),
                counts.inWorkshopCount(),
                periodDays,
                averageMonthlyMaintenances
        );
    }

    private List<MonthlyMaintenance> getMonthlySeries(LocalDate since, LocalDate until) {
        Map<YearMonth, Long> mapCounts = maintenanceReportRepository
                .getMonthlyCounts(toStartOfDay(since), toEndOfDayExclusive(until))
                .stream()
                .collect(Collectors.toMap(
                        r -> YearMonth.of(r.year(), r.month()),
                        MonthlyMaintenance::total
                ));

        List<MonthlyMaintenance> result = new ArrayList<>();
        YearMonth current = YearMonth.from(since);
        YearMonth last = YearMonth.from(until);

        while (!current.isAfter(last)) {
            result.add(new MonthlyMaintenance(
                    current.getYear(),
                    current.getMonthValue(),
                    mapCounts.getOrDefault(current, 0L)
            ));
            current = current.plusMonths(1);
        }
        return result;
    }

    private List<TopCustomer> getTopCustomers(LocalDate since, LocalDate until, int limit) {
        return maintenanceReportRepository.getTopCustomers(
                toStartOfDay(since),
                toEndOfDayExclusive(until),
                PageRequest.of(0, limit)
        );
    }

    private BigDecimal computeAvgMonthly(Long totalMaintenances, long periodDays) {
        return BigDecimal.valueOf(totalMaintenances)
                .multiply(BigDecimal.valueOf(30))
                .divide(BigDecimal.valueOf(periodDays), 2, RoundingMode.HALF_UP);
    }

    private void validateRange(LocalDate since, LocalDate until) {
        if (since.isAfter(until)) {
            throw new InvalidDateRangeException(
                    "La fecha 'since' no puede ser posterior a 'until'");
        }
        if (since.plusMonths(MAX_MONTHS).isBefore(until)) {
            throw new InvalidDateRangeException(
                    "El rango máximo permitido es de 24 meses");
        }
    }

    private OffsetDateTime toStartOfDay(LocalDate date) {
        return date.atStartOfDay(ZONE).toOffsetDateTime();
    }

    private OffsetDateTime toEndOfDayExclusive(LocalDate date) {
        return date.plusDays(1).atStartOfDay(ZONE).toOffsetDateTime();
    }
}