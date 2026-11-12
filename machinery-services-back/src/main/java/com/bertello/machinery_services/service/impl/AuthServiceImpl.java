package com.bertello.machinery_services.service.impl;

import com.bertello.machinery_services.dto.response.AuthenticationResponse;
import com.bertello.machinery_services.dto.request.LoginRequest;
import com.bertello.machinery_services.entity.UserEntity;
import com.bertello.machinery_services.repository.UserRepository;
import com.bertello.machinery_services.security.JwtService;
import com.bertello.machinery_services.service.AuthService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    public AuthenticationResponse login(LoginRequest request) {
        // Delegamos validación de credenciales a Spring
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        UserEntity userEntity = userRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        String token = jwtService.generateAccessToken(userEntity);

        return new AuthenticationResponse(
                userEntity.getId(),
                userEntity.getEmail(),
                userEntity.getRole(),
                token);
    }
}