package com.bertello.machinery_services.service.impl;

import com.bertello.machinery_services.dto.request.CreateCustomerRequest;
import com.bertello.machinery_services.dto.response.CustomerResponse;
import com.bertello.machinery_services.entity.CustomerEntity;
import com.bertello.machinery_services.mapper.CustomerMapper;
import com.bertello.machinery_services.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {
    @Mock
    CustomerRepository customerRepository;
    @Spy
    CustomerMapper customerMapper;
    @InjectMocks
    CustomerServiceImpl customerService;

    @BeforeEach
    void setUp() {

    }

    @Test
    void createCustomer() {
        CreateCustomerRequest r1 = new CreateCustomerRequest("Valentino");
        CustomerEntity cE1 = CustomerEntity.builder()
                .id(1L)
                .fullName("Valentino")
                .maintenanceCount(0)
                .build();
        when(customerRepository.save(any()))
                .thenReturn(cE1);
        CustomerResponse result = customerService.createCustomer(r1);
        assertNotNull(result);
    }

    @Test
    @DisplayName("Cuando fullName es blank, debe pasar null como patrón al repositorio")
    void shouldPassNullWhenFullNameIsBlank() {
        when(customerRepository.findCustomersByFullNameContaining(isNull(), any(Sort.class)))
                .thenReturn(List.of());

        List<CustomerResponse> result = customerService.searchCustomers("   ", Sort.unsorted());

        verify(customerRepository).findCustomersByFullNameContaining(isNull(), any(Sort.class));
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldPassNullWhenFullNameIsNull() {
        when(customerRepository.findCustomersByFullNameContaining(isNull(), any(Sort.class)))
                .thenReturn(List.of());

        customerService.searchCustomers(null, Sort.unsorted());

        verify(customerRepository).findCustomersByFullNameContaining(isNull(), any(Sort.class));
    }

    @Test
    void shouldPassTrimmedNameWhenFullNameIsNotBlank() {
        when(customerRepository.findCustomersByFullNameContaining(eq("Valentino"), any(Sort.class)))
                .thenReturn(List.of());

        customerService.searchCustomers("  Valentino  ", Sort.unsorted());

        verify(customerRepository).findCustomersByFullNameContaining(eq("Valentino"), any(Sort.class));
    }
}