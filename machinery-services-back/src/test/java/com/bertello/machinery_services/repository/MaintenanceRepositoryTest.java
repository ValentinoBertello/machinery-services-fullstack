package com.bertello.machinery_services.repository;

import com.bertello.machinery_services.entity.CustomerEntity;
import com.bertello.machinery_services.entity.MaintenanceEntity;
import com.bertello.machinery_services.enums.Place;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.*;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static com.bertello.machinery_services.config.MaintenanceTestHelper.createMaintenance;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
class MaintenanceRepositoryTest {
    private static final ZoneOffset OFFSET = ZoneOffset.of("-03:00");
    @Container
    @ServiceConnection
    static PostgreSQLContainer postgreSQLContainer =
            new PostgreSQLContainer("postgres:17-alpine");

    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    MaintenanceRepository maintenanceRepository;

    private CustomerEntity c1;
    private CustomerEntity c2;

    @BeforeEach
    void setup() {
        c1 = customerRepository.save(
                CustomerEntity.builder().fullName("Agroempresa Santa Rita").maintenanceCount(0).build());
        c2 = customerRepository.save(
                CustomerEntity.builder().fullName("Suc Noseda").maintenanceCount(0).build());

        MaintenanceEntity s1 = createMaintenance(c1, "2026-01-09T00:00:00-03:00", Place.WORKSHOP, true); // 09/01/2026, 00:00
        MaintenanceEntity s4 = createMaintenance(c1, "2026-01-16T07:34:00-03:00", Place.WORKSHOP, true); // 16/01/2026, 07:34
        MaintenanceEntity s2 = createMaintenance(c1, "2026-01-27T11:34:00-03:00", Place.WORKSHOP, true); // 27/01/2026, 11:34
        MaintenanceEntity s5 = createMaintenance(c1, "2026-02-05T16:34:00-03:00", Place.WORKSHOP, true); // 05/02/2026, 16:34
        MaintenanceEntity s3 = createMaintenance(c1, "2026-04-15T00:02:00-03:00", Place.WORKSHOP, true); // 15/04/2026, 00:02
        MaintenanceEntity s7 = createMaintenance(c1, "2026-02-05T13:30:00-03:00", Place.WORKSHOP, true); // 05/02/2026, 13:30

        MaintenanceEntity s6 = createMaintenance(c2, "2026-02-24T16:34:00-03:00", Place.WORKSHOP, true); // 24/02/2026, 16:34

        maintenanceRepository.saveAll(List.of(
                s1, s2, s3, s4, s5, s6, s7
        ));
    }

    @Test
    void findByCustomerIdAndMaintenanceDateBetweenTest() {
        OffsetDateTime since = LocalDate.of(2026, 1, 9).atStartOfDay(OFFSET).toOffsetDateTime();
        OffsetDateTime until = LocalDate.of(2026, 4, 14).plusDays(1).atStartOfDay(OFFSET).toOffsetDateTime();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("maintenanceDate").descending());

        Page<MaintenanceEntity> result =
                maintenanceRepository.findByCustomerAndDateRange(c1.getId(), since, until, pageable);

        assertEquals(5, result.getContent().size());
        assertEquals(OffsetDateTime.parse("2026-02-05T16:34:00-03:00"), result.getContent().getFirst().getMaintenanceDate());
    }

    @Test
    void countByCustomerIdAndMaintenanceDateAfterTest() {
        OffsetDateTime date = LocalDate.of(2026, 1, 10).atStartOfDay(OFFSET).toOffsetDateTime();

        int result = maintenanceRepository.countByCustomerIdAndMaintenanceDateAfter(c1.getId(), date);

        assertEquals(5, result);
    }

}