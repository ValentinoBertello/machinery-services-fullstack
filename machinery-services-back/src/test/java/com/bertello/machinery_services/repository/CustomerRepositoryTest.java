package com.bertello.machinery_services.repository;

import com.bertello.machinery_services.entity.CustomerEntity;
import com.bertello.machinery_services.enums.Place;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Sort;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.OffsetDateTime;
import java.util.List;

import static com.bertello.machinery_services.config.MaintenanceTestHelper.createMaintenance;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
class CustomerRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgreSQLContainer =
            new PostgreSQLContainer("postgres:17-alpine");

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    MaintenanceRepository maintenanceRepository;

    @Autowired
    TestEntityManager entityManager;

    @BeforeEach
    void setup() {
        CustomerEntity c1 = CustomerEntity.builder()
                .fullName("Agroempresa Santa Rita")
                .maintenanceCount(0)
                .build();
        CustomerEntity c2 = CustomerEntity.builder()
                .fullName("Suc Noseda")
                .maintenanceCount(0)
                .deletedAt(OffsetDateTime.parse("2026-10-01T12:30:00Z"))
                .build();
        CustomerEntity c3 = CustomerEntity.builder()
                .fullName("Los Bernardo sa")
                .maintenanceCount(15)
                .build();
        CustomerEntity c4 = CustomerEntity.builder()
                .fullName("Gallo Wilmar")
                .maintenanceCount(26)
                .build();
        CustomerEntity c5 = CustomerEntity.builder()
                .fullName("Getagro SA")
                .maintenanceCount(0)
                .build();
        CustomerEntity c6 = CustomerEntity.builder()
                .fullName("Valentino Bertello")
                .maintenanceCount(0)
                .deletedAt(OffsetDateTime.parse("2026-10-01T12:30:00Z"))
                .build();
        customerRepository.saveAll(List.of(c1, c2, c3, c4, c5, c6));
    }

    @Test
    void testFindCustomersByFullNameBlank() {
        List<CustomerEntity> response = customerRepository
                .findCustomersByFullNameContaining("", Sort.by("fullName").ascending());
        assertEquals(4, response.size());
        assertEquals("Agroempresa Santa Rita", response.getFirst().getFullName());
    }

    @Test
    void testFindCustomersByFullNameNull() {
        // null ⇒ sin filtro de nombre; solo excluye deletedAt
        List<CustomerEntity> response = customerRepository
                .findCustomersByFullNameContaining(null, Sort.by("fullName").ascending());
        assertEquals(4, response.size());
    }

    @Test
    void testFindCustomersByFullNameContaining() {
        List<CustomerEntity> response = customerRepository
                .findCustomersByFullNameContaining("G", Sort.by("fullName").ascending());
        assertEquals(3, response.size());
        assertEquals("Agroempresa Santa Rita", response.get(0).getFullName());
        assertEquals("Gallo Wilmar", response.get(1).getFullName());
        assertEquals("Getagro SA", response.get(2).getFullName());
    }

    @Test
    void testSortByMaintenanceCountDesc() {
        Sort sort = Sort.by(Sort.Direction.DESC, "maintenanceCount")
                .and(Sort.by(Sort.Direction.ASC, "id"));
        List<CustomerEntity> response = customerRepository
                .findCustomersByFullNameContaining(null, sort);
        assertEquals(4, response.size());
        assertEquals("Gallo Wilmar", response.get(0).getFullName());      // 26
        assertEquals("Los Bernardo sa", response.get(1).getFullName());   // 15
        // los dos restantes tienen maintenanceCount = 0 (orden por id, no importa cuál)
    }

    @Test
    @DisplayName("refreshMaintenanceCounts recalcula el contador de mantenimientos de cada cliente, partiendo de " +
            "una fecha determinada")
    void refreshMaintenanceCountsTest() {
        customerRepository.deleteAll();

        OffsetDateTime since = OffsetDateTime.parse("2025-01-01T00:00:00-03:00");

        CustomerEntity c1 = customerRepository.save(CustomerEntity.builder()
                .fullName("Cliente A")
                .maintenanceCount(99) // Valor a corregir
                .build());
        CustomerEntity c2 = customerRepository.save(CustomerEntity.builder()
                .fullName("Cliente B")
                .maintenanceCount(99)
                .build());

        // --- Bordes alrededor de since ---
        maintenanceRepository.save(createMaintenance(c1, "2025-01-01T00:00:00-03:00", Place.WORKSHOP, true)); // No cuenta: exactamente since (> estricto)
        maintenanceRepository.save(createMaintenance(c1, "2025-01-01T03:00:00Z", Place.WORKSHOP, true));      // No cuenta: mismo instante que since, en UTC
        maintenanceRepository.save(createMaintenance(c1, "2025-01-01T02:59:59Z", Place.WORKSHOP, true));      // No cuenta: 23:59:59 del 31/12 en -03:00 (1 seg antes)
        maintenanceRepository.save(createMaintenance(c1, "2025-01-01T00:00:01-03:00", Place.WORKSHOP, true)); // Cuenta: 1 segundo después de since

        // --- Fechas normales con horas distintas ---
        maintenanceRepository.save(createMaintenance(c1, "2025-06-01T23:59:59-03:00", Place.WORKSHOP, true)); // Cuenta
        maintenanceRepository.save(createMaintenance(c1, "2025-08-01T12:30:00-03:00", Place.WORKSHOP, true)); // Cuenta

        // --- Mucho antes ---
        maintenanceRepository.save(createMaintenance(c1, "2024-06-01T10:00:00-03:00", Place.WORKSHOP, true)); // No cuenta
        entityManager.flush();
        int result = customerRepository.refreshMaintenanceCounts(since);
        entityManager.clear();

        assertEquals(2, result);
        assertEquals(3, customerRepository.findById(c1.getId()).orElseThrow().getMaintenanceCount());
        assertEquals(0, customerRepository.findById(c2.getId()).orElseThrow().getMaintenanceCount());
    }
}