package com.bertello.machinery_services.controller;

import com.bertello.machinery_services.dto.response.AuthenticationResponse;
import com.bertello.machinery_services.dto.response.CustomerResponse;
import com.bertello.machinery_services.dto.response.MaintenanceResponse;
import com.bertello.machinery_services.enums.Place;
import com.bertello.machinery_services.service.AuthService;
import com.bertello.machinery_services.service.CustomerService;
import com.bertello.machinery_services.service.MaintenanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class ControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private CustomerService customerService;

    @Mock
    private MaintenanceService maintenanceService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(
                new AuthController(authService),
                new CustomerController(customerService),
                new MaintenanceController(maintenanceService)
        ).build();
    }

    @Test
    void loginShouldReturnToken() throws Exception {
        when(authService.login(any()))
                .thenReturn(new AuthenticationResponse(
                        1L, "admin@test.com", "ADMIN", "token-test"
                ));

        mockMvc.perform(post("/auth")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "admin@test.com",
                                  "password": "12345"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@test.com"))
                .andExpect(jsonPath("$.token").value("token-test"));

        verify(authService).login(any());
    }

    @Test
    void getCustomerShouldReturnCustomer() throws Exception {
        when(customerService.getCustomerById(1L))
                .thenReturn(new CustomerResponse(
                        1L, "Cliente Test", 3, null
                ));

        mockMvc.perform(get("/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fullName").value("Cliente Test"))
                .andExpect(jsonPath("$.maintenanceCount").value(3));

        verify(customerService).getCustomerById(1L);
    }

    @Test
    void createMaintenanceShouldReturnCreated() throws Exception {
        when(maintenanceService.createMaintenance(any()))
                .thenReturn(new MaintenanceResponse(
                        10L,
                        null,
                        null,
                        Place.WORKSHOP,
                        "MF 6713",
                        "Cambio de aceite",
                        1L,
                        "Cliente Test",
                        null
                ));

        mockMvc.perform(post("/maintenances")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "maintenanceDate": "2026-10-20T10:00:00-03:00",
                                  "place": "WORKSHOP",
                                  "model": "MF 6713",
                                  "description": "Cambio de aceite",
                                  "customerId": 1
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.place").value("WORKSHOP"));

        verify(maintenanceService).createMaintenance(any());
    }
}