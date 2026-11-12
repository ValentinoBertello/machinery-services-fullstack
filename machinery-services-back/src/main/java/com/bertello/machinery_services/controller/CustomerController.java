package com.bertello.machinery_services.controller;

import com.bertello.machinery_services.annotation.SortParam;
import com.bertello.machinery_services.dto.request.CreateCustomerRequest;
import com.bertello.machinery_services.dto.response.CustomerResponse;
import com.bertello.machinery_services.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@RequestBody @Valid CreateCustomerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.createCustomer(request));
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> searchCustomers(
            @RequestParam(required = false) String fullName,
            @SortParam Sort sort) {
        return ResponseEntity.ok(customerService.searchCustomers(fullName, sort));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }
}
