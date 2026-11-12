package com.bertello.machinery_services.dto.response;

public record AuthenticationResponse(
        Long id,
        String email,
        String role,
        String token
){}
