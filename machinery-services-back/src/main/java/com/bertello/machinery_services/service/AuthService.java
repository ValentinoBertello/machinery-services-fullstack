package com.bertello.machinery_services.service;

import com.bertello.machinery_services.dto.response.AuthenticationResponse;
import com.bertello.machinery_services.dto.request.LoginRequest;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    AuthenticationResponse login(LoginRequest request);
}
