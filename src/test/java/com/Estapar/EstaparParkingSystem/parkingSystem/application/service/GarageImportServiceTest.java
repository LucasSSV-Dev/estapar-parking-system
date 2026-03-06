package com.Estapar.EstaparParkingSystem.parkingSystem.application.service;

import com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto.GarageConfigDTO;
import com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto.GarageConfigRequestDTO;
import com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto.ParkingSpotConfigDTO;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.model.Garage;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.repository.GarageRepository;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.repository.ParkingSpotRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GarageImportServiceTest {

    @Mock
    private RestClient restClient;

    @Mock
    private GarageRepository garageRepository;

    @Mock
    private ParkingSpotRepository parkingSpotRepository;

    @InjectMocks
    private GarageImportService garageImportService;

    @Test
    @DisplayName("Should import Garage successfully")
    void GarageImportServiceTest_importGarageTestCase1() {

        // Arrange
        GarageConfigDTO garageDTO =
                new GarageConfigDTO("A", new BigDecimal("10.00"), 100);

        ParkingSpotConfigDTO spotDTO =
                new ParkingSpotConfigDTO("A", -22.9, -43.2);

        GarageConfigRequestDTO response =
                new GarageConfigRequestDTO(
                        List.of(garageDTO),
                        List.of(spotDTO)
                );

        RestClient.RequestHeadersUriSpec requestMock = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.ResponseSpec responseMock = mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(requestMock);
        when(requestMock.uri(anyString())).thenReturn(requestMock);
        when(requestMock.retrieve()).thenReturn(responseMock);
        when(responseMock.body(GarageConfigRequestDTO.class)).thenReturn(response);

        when(garageRepository.findAll()).thenReturn(List.of());
        when(garageRepository.findBySector(anyString())).thenReturn(Optional.of(new Garage()));

        // Act
        garageImportService.importGarage();

        // Assert
        verify(restClient).get();
        verify(garageRepository).saveAll(anyList());
        verify(parkingSpotRepository).saveAll(anyList());
    }

}