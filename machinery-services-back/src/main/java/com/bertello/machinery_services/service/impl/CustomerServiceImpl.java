package com.bertello.machinery_services.service.impl;

import com.bertello.machinery_services.dto.request.CreateCustomerRequest;
import com.bertello.machinery_services.dto.response.CustomerResponse;
import com.bertello.machinery_services.entity.CustomerEntity;
import com.bertello.machinery_services.mapper.CustomerMapper;
import com.bertello.machinery_services.repository.CustomerRepository;
import com.bertello.machinery_services.service.CustomerService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    @Transactional
    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        CustomerEntity customerEntity = customerMapper.toEntity(request);
        return customerMapper.toResponse(customerRepository.save(customerEntity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> searchCustomers(String fullName, Sort sort) {
        String fullNameSearch  = (fullName == null || fullName.isBlank()) ? null : fullName.trim();
        List<CustomerEntity> entities = customerRepository.findCustomersByFullNameContaining(fullNameSearch, sort);
        return customerMapper.toResponseList(entities);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(Long id) {
        CustomerEntity customerEntity = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));
        return customerMapper.toResponse(customerEntity);
    }

    @Scheduled(cron = "0 0 3 * * *", zone = "America/Argentina/Cordoba")
    @Transactional
    public void refreshMaintenanceCounts() {
        OffsetDateTime since = OffsetDateTime.now().minusMonths(6);
        int updated = customerRepository.refreshMaintenanceCounts(since);
    }
}
