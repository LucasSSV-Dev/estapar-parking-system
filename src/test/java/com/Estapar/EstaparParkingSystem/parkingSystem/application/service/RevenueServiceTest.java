package com.Estapar.EstaparParkingSystem.parkingSystem.application.service;

import com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto.RevenueRequestDTO;
import com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto.RevenueResponseDTO;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.enums.CurrencyEnum;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.enums.EventTypeEnum;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.model.Revenue;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.repository.ParkingEventRepository;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.repository.RevenueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RevenueServiceTest {

    @Mock
    private ParkingEventRepository parkingEventRepository;

    @Mock
    private RevenueRepository revenueRepository;

    @InjectMocks
    private RevenueService service;

    private RevenueRequestDTO requestDTO;
    private Revenue revenue;

    @BeforeEach
    void setUp() {

        requestDTO = new RevenueRequestDTO(
                LocalDate.now(),
                "A"
                );

        revenue = new Revenue();
        revenue.setSector("A");
        revenue.setDate(requestDTO.date());
        revenue.setAmount(new BigDecimal("100.00"));
        revenue.setCurrencyCode(CurrencyEnum.BRL);
    }

    @Test
    @DisplayName("Should create new revenue when none exists for sector and date")
    void RevenueServiceTest_calculateRevenue_case01() {

        // Arrange
        when(parkingEventRepository.sumRevenueByDateAndSectorAndEventType(
                any(), any(), anyString(), any()))
                .thenReturn(new BigDecimal("100.00"));

        when(revenueRepository.findBySectorAndDate(anyString(), any()))
                .thenReturn(Optional.empty());

        when(revenueRepository.save(any())).thenReturn(revenue);

        // Act
        RevenueResponseDTO response = service.calculateRevenue(requestDTO);

        // Assert
        assertNotNull(response);
        assertEquals(new BigDecimal("100.00"), response.amount());
        assertEquals(CurrencyEnum.BRL, response.currency());

        verify(revenueRepository).save(any(Revenue.class));
    }

    @Test
    @DisplayName("Should update existing revenue when revenue already exists")
    void RevenueServiceTest_calculateRevenue_case02() {

        // Arrange
        when(parkingEventRepository.sumRevenueByDateAndSectorAndEventType(
                any(), any(), anyString(), any()))
                .thenReturn(new BigDecimal("200.00"));

        when(revenueRepository.findBySectorAndDate(anyString(), any()))
                .thenReturn(Optional.of(revenue));

        when(revenueRepository.save(any())).thenReturn(revenue);

        // Act
        RevenueResponseDTO response = service.calculateRevenue(requestDTO);

        // Assert
        assertNotNull(response);
        assertEquals(new BigDecimal("200.00"), revenue.getAmount());

        verify(revenueRepository).save(revenue);
    }

    @Test
    @DisplayName("Should call repository to calculate revenue amount")
    void RevenueServiceTest_calculateRevenue_case03() {

        // Arrange
        when(parkingEventRepository.sumRevenueByDateAndSectorAndEventType(
                any(), any(), anyString(), any()))
                .thenReturn(new BigDecimal("150.00"));

        when(revenueRepository.findBySectorAndDate(anyString(), any()))
                .thenReturn(Optional.empty());

        // Act
        service.calculateRevenue(requestDTO);

        // Assert
        verify(parkingEventRepository)
                .sumRevenueByDateAndSectorAndEventType(
                        any(),
                        any(),
                        eq("A"),
                        any()
                );
    }

    @Test
    @DisplayName("Should save revenue after calculating amount")
    void RevenueServiceTest_calculateRevenue_case04() {

        // Arrange
        when(parkingEventRepository.sumRevenueByDateAndSectorAndEventType(
                any(), any(), anyString(), any()))
                .thenReturn(new BigDecimal("120.00"));

        when(revenueRepository.findBySectorAndDate(anyString(), any()))
                .thenReturn(Optional.empty());

        when(revenueRepository.save(any())).thenReturn(revenue);

        // Act
        service.calculateRevenue(requestDTO);

        // Assert
        verify(revenueRepository, times(1))
                .save(any(Revenue.class));
    }

    @Test
    @DisplayName("Should return RevenueResponseDTO with correct values")
    void RevenueServiceTest_calculateRevenue_case05() {

        // Arrange
        when(parkingEventRepository.sumRevenueByDateAndSectorAndEventType(
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                anyString(),
                eq(EventTypeEnum.EXIT)))
                .thenReturn(new BigDecimal("300.00"));

        when(revenueRepository.findBySectorAndDate(anyString(), any(LocalDate.class)))
                .thenReturn(Optional.empty());

        when(revenueRepository.save(any(Revenue.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        RevenueResponseDTO response = service.calculateRevenue(requestDTO);

        // Assert
        assertNotNull(response);
        assertEquals(new BigDecimal("300.00"), response.amount());
        assertEquals(CurrencyEnum.BRL, response.currency());
        assertNotNull(response.timestamp());
    }
}