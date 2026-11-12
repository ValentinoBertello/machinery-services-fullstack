package com.bertello.machinery_services.annotation;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Parameter(
        name = "sort",
        in = ParameterIn.QUERY,
        description = "Orden: propiedad,(asc|desc).",
        array = @ArraySchema(schema = @Schema(type = "string", example = "maintenanceDate,desc"))
)
public @interface SortParam {}