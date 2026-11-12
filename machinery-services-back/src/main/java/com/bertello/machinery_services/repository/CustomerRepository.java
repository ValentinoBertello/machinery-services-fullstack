package com.bertello.machinery_services.repository;

import com.bertello.machinery_services.entity.CustomerEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {

    @Query("""
    SELECT c FROM CustomerEntity c
    WHERE c.deletedAt IS NULL
      AND (:fullName IS NULL OR lower(c.fullName) LIKE lower(concat('%', cast(:fullName as string), '%')))
""")
    List<CustomerEntity> findCustomersByFullNameContaining(@Param("fullName") String fullName, Sort sort);

    @Modifying
    @Query(value = """
        UPDATE customers c
        SET maintenance_count = (
                SELECT COUNT(*)
                FROM maintenances m
                WHERE m.customer_id = c.id
                    AND m.deleted_at IS NULL
                    AND m.maintenance_date > :since
        ),
        maintenance_count_updated_at = now()
        WHERE c.deleted_at IS NULL
""", nativeQuery = true)
    int refreshMaintenanceCounts(@Param("since") OffsetDateTime since);
}
