package com.bertello.machinery_services.repository;

import com.bertello.machinery_services.dto.report.MaintenanceSummaryRaw;
import com.bertello.machinery_services.dto.report.MonthlyMaintenance;
import com.bertello.machinery_services.dto.report.TopCustomer;
import com.bertello.machinery_services.entity.MaintenanceEntity;
import com.bertello.machinery_services.enums.Place;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface MaintenanceReportRepository extends JpaRepository<MaintenanceEntity, Long> {
    @Query("""
            SELECT new com.bertello.machinery_services.dto.report.MaintenanceSummaryRaw(
            COUNT(m),
            COUNT(DISTINCT m.customer.id),
            COALESCE(SUM(CASE WHEN m.invoiceDate IS NULL THEN 1 ELSE 0 END), 0),
            COALESCE(SUM(CASE WHEN m.place = :fieldPlace THEN 1 ELSE 0 END), 0),
            COALESCE(SUM(CASE WHEN m.place = :workshopPlace THEN 1 ELSE 0 END), 0)
            )
            FROM MaintenanceEntity m
            WHERE m.maintenanceDate >= :since AND m.maintenanceDate <  :until
            """)
    MaintenanceSummaryRaw getMaintenanceSummary(@Param("since") OffsetDateTime since,
                                                @Param("until") OffsetDateTime until,
                                                @Param("fieldPlace") Place fieldPlace,
                                                @Param("workshopPlace") Place workshopPlace);

    @Query("""
        SELECT new com.bertello.machinery_services.dto.report.MonthlyMaintenance(
            YEAR(m.maintenanceDate),
            MONTH(m.maintenanceDate),
            COUNT(m)
        )
        FROM MaintenanceEntity m
        WHERE m.maintenanceDate >= :since AND m.maintenanceDate < :until
        GROUP BY YEAR(m.maintenanceDate), MONTH(m.maintenanceDate)
""")
    List<MonthlyMaintenance> getMonthlyCounts(
            @Param("since") OffsetDateTime since,
            @Param("until") OffsetDateTime until
    );

    @Query("""
        SELECT new com.bertello.machinery_services.dto.report.TopCustomer(
            c.id,
            c.fullName,
            COUNT(m),
            MAX(m.maintenanceDate)
        )
        FROM MaintenanceEntity m JOIN m.customer c
        WHERE m.maintenanceDate >= :since AND m.maintenanceDate < :until
        GROUP BY c.id, c.fullName
        ORDER BY COUNT(m) DESC, MAX(m.maintenanceDate) DESC
""")
    List<TopCustomer> getTopCustomers(
            @Param("since") OffsetDateTime since,
            @Param("until") OffsetDateTime until,
            Pageable pageable
    );
}
