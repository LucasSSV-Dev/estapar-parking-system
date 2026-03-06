package com.Estapar.EstaparParkingSystem.parkingSystem.application.api.controller;

import com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto.RevenueRequestDTO;
import com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto.RevenueResponseDTO;
import com.Estapar.EstaparParkingSystem.parkingSystem.application.service.RevenueService;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.enums.CurrencyEnum;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RevenueController.class)
class RevenueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean //Infelizmente não consegui usar o MockitoBean D'=
    private RevenueService revenueService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should return revenue successfully")
    void RevenueControllerTest_getRevenue_case01() throws Exception {

        RevenueRequestDTO requestDTO = new RevenueRequestDTO(
                LocalDate.of(2025, 1, 1),
                "A"
        );

        RevenueResponseDTO responseDTO = new RevenueResponseDTO(
                new BigDecimal("100.00"),
                CurrencyEnum.BRL,
                Instant.now()
        );

        when(revenueService.calculateRevenue(any()))
                .thenReturn(responseDTO);

        mockMvc.perform(
                        get("/revenue")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(100.00))
                .andExpect(jsonPath("$.currency").value("BRL"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(revenueService).calculateRevenue(any());
    }

    @Test
    @DisplayName("Should call service when endpoint is accessed")
    void RevenueControllerTest_getRevenue_case02() throws Exception {

        RevenueRequestDTO requestDTO = new RevenueRequestDTO(
                LocalDate.of(2025, 1, 1),
                "A"
        );

        RevenueResponseDTO responseDTO = new RevenueResponseDTO(
                new BigDecimal("200.00"),
                CurrencyEnum.BRL,
                Instant.now()
        );

        when(revenueService.calculateRevenue(any()))
                .thenReturn(responseDTO);

        mockMvc.perform(
                        get("/revenue")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO))
                )
                .andExpect(status().isOk());

        verify(revenueService).calculateRevenue(any());
    }
}