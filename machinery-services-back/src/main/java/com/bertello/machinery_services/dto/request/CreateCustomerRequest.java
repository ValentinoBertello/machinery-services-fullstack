package com.bertello.machinery_services.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCustomerRequest(
        @NotBlank(message = "Nombre es obligatorio")
        @Size(max = 255)
        String fullName
) {}
