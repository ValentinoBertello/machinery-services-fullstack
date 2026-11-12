package com.bertello.machinery_services.repository;

import com.bertello.machinery_services.entity.MaintenanceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;

@Repository
public interface MaintenanceRepository extends JpaRepository<MaintenanceEntity, Long> {
    @Query("""
        SELECT m FROM MaintenanceEntity m
        WHERE m.customer.id = :customerId
        AND m.maintenanceDate >= :since
        AND m.maintenanceDate < :until
        AND m.deletedAt IS NULL
""")
    Page<MaintenanceEntity> findByCustomerAndDateRange(
            @Param("customerId") Long customerId,
            @Param("since") OffsetDateTime since,
            @Param("until") OffsetDateTime until,
            Pageable pageable
    );

    int countByCustomerIdAndMaintenanceDateAfter(Long customerId, OffsetDateTime date);

    @EntityGraph(attributePaths = {"customer"})
    @Query("""
    SELECT m FROM MaintenanceEntity m
    WHERE m.maintenanceDate >= :since
      AND m.maintenanceDate <  :until
      AND m.deletedAt IS NULL
""")
    Page<MaintenanceEntity> findByMaintenanceDateBetween(
            @Param("since") OffsetDateTime since,
            @Param("until") OffsetDateTime until,
            Pageable pageable
    );
}
