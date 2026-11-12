package com.bertello.machinery_services.mapper;

import com.bertello.machinery_services.dto.request.CreateCustomerRequest;
import com.bertello.machinery_services.dto.response.CustomerResponse;
import com.bertello.machinery_services.entity.CustomerEntity;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomerMapperTest {
    private final CustomerMapper mapper = new CustomerMapper();

    @Test
    void shouldMapRequestToEntityAndTrimName() {
        // Arrange
        CreateCustomerRequest request = new CreateCustomerRequest("  Valentino  ");

        // Act
        CustomerEntity entity = mapper.toEntity(request);

        // Assert
        assertNotNull(entity);
        assertEquals("Valentino", entity.getFullName()); // Verifica el trim
    }

    @Test
    void shouldMapEntityToResponse() {
        // Arrange
        CustomerEntity entity = CustomerEntity.builder()
                .id(1L)
                .fullName("Valentino")
                .maintenanceCount(5)
                .createdAt(OffsetDateTime.now())
                .build();

        // Act
        CustomerResponse response = mapper.toResponse(entity);

        // Assert
        assertEquals(1L, response.id());
        assertEquals("Valentino", response.fullName());
        assertEquals(5, response.maintenanceCount());
        assertNotNull(response.createdAt());
    }

    @Test
    void shouldMapListOfEntitiesToResponseList() {
        CustomerEntity entity = CustomerEntity.builder()
                .id(1L)
                .fullName("Test")
                .maintenanceCount(0)
                .build();

        // Act
        List<CustomerResponse> responses = mapper.toResponseList(List.of(entity));

        // Assert
        assertEquals(1, responses.size());
        assertEquals("Test", responses.get(0).fullName());
    }
}