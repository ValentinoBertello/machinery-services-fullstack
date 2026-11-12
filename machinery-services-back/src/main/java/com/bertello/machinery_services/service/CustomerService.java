package com.bertello.machinery_services.service;

import com.bertello.machinery_services.dto.request.CreateCustomerRequest;
import com.bertello.machinery_services.dto.response.CustomerResponse;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CustomerService {
    CustomerResponse createCustomer(CreateCustomerRequest request);
    List<CustomerResponse> searchCustomers(String fullName, Sort sort);
    CustomerResponse getCustomerById(Long id);
}
