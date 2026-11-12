package com.bertello.machinery_services.dto.request;

import com.bertello.machinery_services.enums.Place;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

public record UpdateMaintenanceRequest(
        @NotNull(message = "Fecha es obligatorio")
        OffsetDateTime maintenanceDate,

        OffsetDateTime invoiceDate,

        @NotNull(message = "Lugar es obligatorio")
        Place place,

        @NotBlank(message = "Model es obligatorio")
        @Size(max = 255)
        String model,

        @NotBlank(message = "Descripción es obligatorio")
        String description
) {}
