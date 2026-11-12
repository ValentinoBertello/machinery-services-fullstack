package com.bertello.machinery_services.repository;

import com.bertello.machinery_services.dto.report.MaintenanceSummaryRaw;
import com.bertello.machinery_services.dto.report.MonthlyMaintenance;
import com.bertello.machinery_services.dto.report.TopCustomer;
import com.bertello.machinery_services.entity.CustomerEntity;
import com.bertello.machinery_services.entity.MaintenanceEntity;
import com.bertello.machinery_services.enums.Place;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.PageRequest;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.OffsetDateTime;
import java.util.List;

import static com.bertello.machinery_services.config.MaintenanceTestHelper.createMaintenance;
import static com.bertello.machinery_services.config.MaintenanceTestHelper.persistBaseMaintenances;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
class MaintenanceReportRepositoryTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer postgreSQLContainer =
            new PostgreSQLContainer("postgres:17-alpine");

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    MaintenanceReportRepository maintenanceReportRepository;

    private CustomerEntity c1;
    private CustomerEntity c2;

    @BeforeEach
    void setup() {
        c1 = customerRepository.save(
                CustomerEntity.builder()
                        .fullName("Agroempresa Santa Rita")
                        .maintenanceCount(0)
                        .build()
        );

        c2 = customerRepository.save(
                CustomerEntity.builder()
                        .fullName("Suc Noseda")
                        .maintenanceCount(0)
                        .build()
        );
    }

    @Nested
    @DisplayName("getMaintenanceSummary")
    class GetMaintenanceSummary {

        @BeforeEach
        void createBaseMaintenances() {
            persistBaseMaintenances(maintenanceReportRepository, c1, c2);
        }

        @Test
        void getMaintenanceSummaryCountsActiveMaintenancesInsideRange() {
            OffsetDateTime since =
                    OffsetDateTime.parse("2026-01-01T00:00:00-03:00");
            OffsetDateTime until =
                    OffsetDateTime.parse("2026-02-01T00:00:00-03:00");

            MaintenanceSummaryRaw result =
                    maintenanceReportRepository.getMaintenanceSummary(
                            since,
                            until,
                            Place.FIELD,
                            Place.WORKSHOP
                    );

            assertAll(
                    () -> assertEquals(3L, result.totalMaintenances()),
                    () -> assertEquals(2L, result.distinctCustomers()),
                    () -> assertEquals(2L, result.notInvoicedCount()),
                    () -> assertEquals(2L, result.inFieldCount()),
                    () -> assertEquals(1L, result.inWorkshopCount())
            );
        }

        @Test
        void getMaintenanceSummaryReturnsZerosWhenRangeHasNoMaintenances() {
            OffsetDateTime since =
                    OffsetDateTime.parse("2027-01-01T00:00:00-03:00");
            OffsetDateTime until =
                    OffsetDateTime.parse("2027-02-01T00:00:00-03:00");

            MaintenanceSummaryRaw result =
                    maintenanceReportRepository.getMaintenanceSummary(
                            since,
                            until,
                            Place.FIELD,
                            Place.WORKSHOP
                    );

            assertAll(
                    () -> assertEquals(0L, result.totalMaintenances()),
                    () -> assertEquals(0L, result.distinctCustomers()),
                    () -> assertEquals(0L, result.notInvoicedCount()),
                    () -> assertEquals(0L, result.inFieldCount()),
                    () -> assertEquals(0L, result.inWorkshopCount())
            );
        }
    }

    @Nested
    @DisplayName("getMonthlyCounts")
    class GetMonthlyCounts {

        @BeforeEach
        void createBaseMaintenances() {
            persistBaseMaintenances(maintenanceReportRepository, c1, c2);
        }

        @Test
        void getMonthlyCountsGroupsMaintenancesByYearAndMonth() {
            /*
             * En Argentina este mantenimiento pertenece al 31 de enero.
             *
             * Sin embargo, el mismo instante expresado en UTC es:
             * 2026-02-01T02:30:00Z
             *
             * Este dato permite comprobar que YEAR y MONTH utilizan la zona horaria
             * argentina configurada para PostgreSQL.
             */
            MaintenanceEntity endOfJanuary = createMaintenance(
                    c1,
                    "2026-01-31T23:30:00-03:00",
                    Place.FIELD,
                    true
            );

            maintenanceReportRepository.saveAndFlush(endOfJanuary);

            OffsetDateTime since =
                    OffsetDateTime.parse("2025-12-01T00:00:00-03:00");
            OffsetDateTime until =
                    OffsetDateTime.parse("2026-03-01T00:00:00-03:00");

            List<MonthlyMaintenance> result =
                    maintenanceReportRepository.getMonthlyCounts(since, until);

            assertThat(result).containsExactlyInAnyOrder(
                    new MonthlyMaintenance(2025, 12, 1L),
                    new MonthlyMaintenance(2026, 1, 4L),
                    new MonthlyMaintenance(2026, 2, 1L)
            );
        }

        @Test
        void getMonthlyCountsRespectsHalfOpenDateRange() {
            OffsetDateTime since =
                    OffsetDateTime.parse("2026-01-01T00:00:00-03:00");
            OffsetDateTime until =
                    OffsetDateTime.parse("2026-02-01T00:00:00-03:00");

            List<MonthlyMaintenance> result =
                    maintenanceReportRepository.getMonthlyCounts(since, until);

            assertAll(
                    () -> assertEquals(1, result.size()),
                    () -> assertEquals(
                            new MonthlyMaintenance(2026, 1, 3L),
                            result.getFirst()
                    )
            );
        }
    }

    @Nested
    @DisplayName("getTopCustomers")
    class GetTopCustomers {

        @BeforeEach
        void createBaseMaintenances() {
            persistBaseMaintenances(maintenanceReportRepository, c1, c2);
        }

        @Test
        void getTopCustomersOrdersByCountThenByLatestMaintenanceDate() {
            CustomerEntity c3 = customerRepository.save(
                    CustomerEntity.builder()
                            .fullName("El Trébol")
                            .maintenanceCount(0)
                            .build()
            );
            CustomerEntity c4 = customerRepository.save(
                    CustomerEntity.builder()
                            .fullName("La Esperanza")
                            .maintenanceCount(0)
                            .build()
            );

            maintenanceReportRepository.saveAll(List.of(
                    createMaintenance(c3, "2026-01-05T10:00:00-03:00", Place.FIELD, false),
                    createMaintenance(c3, "2026-01-15T10:00:00-03:00", Place.FIELD, false),
                    createMaintenance(c3, "2026-01-25T10:00:00-03:00", Place.FIELD, false),
                    createMaintenance(c4, "2026-01-08T10:00:00-03:00", Place.FIELD, false),
                    createMaintenance(c4, "2026-01-25T18:00:00-03:00", Place.FIELD, false)
            ));
            maintenanceReportRepository.flush();

            OffsetDateTime since = OffsetDateTime.parse("2026-01-01T00:00:00-03:00");
            OffsetDateTime until = OffsetDateTime.parse("2026-02-01T00:00:00-03:00");

            List<TopCustomer> result = maintenanceReportRepository.getTopCustomers(
                    since, until, PageRequest.of(0, 10));

            assertThat(result)
                    .extracting(TopCustomer::fullName, TopCustomer::totalMaintenances)
                    .containsExactly(
                            tuple("El Trébol", 3L),
                            tuple("La Esperanza", 2L),
                            tuple("Agroempresa Santa Rita", 2L),
                            tuple("Suc Noseda", 1L)
                    );
        }

        @Test
        void getTopCustomersReturnsEmptyListWhenRangeHasNoMaintenances() {
            OffsetDateTime since = OffsetDateTime.parse("2027-01-01T00:00:00-03:00");
            OffsetDateTime until = OffsetDateTime.parse("2027-02-01T00:00:00-03:00");

            List<TopCustomer> result = maintenanceReportRepository.getTopCustomers(
                    since, until, PageRequest.of(0, 10));

            assertThat(result).isEmpty();
        }

        @Test
        void getTopCustomersRespectsPageable() {
            OffsetDateTime since = OffsetDateTime.parse("2026-01-01T00:00:00-03:00");
            OffsetDateTime until = OffsetDateTime.parse("2026-02-01T00:00:00-03:00");

            List<TopCustomer> topOne = maintenanceReportRepository.getTopCustomers(
                    since, until, PageRequest.of(0, 1));

            assertThat(topOne).hasSize(1);
            assertThat(topOne.getFirst().customerId()).isEqualTo(c1.getId());
            assertThat(topOne.getFirst().totalMaintenances()).isEqualTo(2L);
        }
    }
}