package com.bertello.machinery_services.mapper;

import com.bertello.machinery_services.dto.request.CreateMaintenanceRequest;
import com.bertello.machinery_services.dto.response.MaintenanceResponse;
import com.bertello.machinery_services.entity.CustomerEntity;
import com.bertello.machinery_services.entity.MaintenanceEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MaintenanceMapper {
    public MaintenanceEntity toEntity(CreateMaintenanceRequest request, CustomerEntity customerEntity) {
        MaintenanceEntity entity = new MaintenanceEntity();
        entity.setMaintenanceDate(request.maintenanceDate());
        entity.setInvoiceDate(request.invoiceDate());
        entity.setPlace(request.place());
        entity.setModel(request.model());
        entity.setDescription(request.description());
        entity.setCustomer(customerEntity);
        return entity;
    }

    public MaintenanceResponse toResponse(MaintenanceEntity entity) {
        return new MaintenanceResponse(
                entity.getId(),
                entity.getMaintenanceDate(),
                entity.getInvoiceDate(),
                entity.getPlace(),
                entity.getModel(),
                entity.getDescription(),
                entity.getCustomer().getId(),
                entity.getCustomer().getFullName(),
                entity.getCreatedAt()
        );
    }

    public List<MaintenanceResponse> toResponseList(List<MaintenanceEntity> entities) {
        List<MaintenanceResponse> responses = new ArrayList<>();
        for (MaintenanceEntity entity : entities) {
            responses.add(toResponse(entity));
        }
        return responses;
    }
}
