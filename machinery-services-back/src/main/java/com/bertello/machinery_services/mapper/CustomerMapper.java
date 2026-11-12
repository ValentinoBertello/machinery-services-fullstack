package com.bertello.machinery_services.mapper;

import com.bertello.machinery_services.dto.request.CreateCustomerRequest;
import com.bertello.machinery_services.dto.response.CustomerResponse;
import com.bertello.machinery_services.entity.CustomerEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CustomerMapper {
    public CustomerEntity toEntity(CreateCustomerRequest request) {
        CustomerEntity entity = new CustomerEntity();
        entity.setFullName(request.fullName().trim());
        return entity;
    }

    public CustomerResponse toResponse(CustomerEntity entity) {
        return new CustomerResponse(
                entity.getId(),
                entity.getFullName(),
                entity.getMaintenanceCount(),
                entity.getCreatedAt()
        );
    }

    public List<CustomerResponse> toResponseList(List<CustomerEntity> entities) {
        List<CustomerResponse> result = new ArrayList<>();
        for (CustomerEntity e : entities) {
            result.add(toResponse(e));
        }
        return result;
    }
}
