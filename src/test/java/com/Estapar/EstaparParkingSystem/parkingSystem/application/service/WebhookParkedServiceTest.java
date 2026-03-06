package com.Estapar.EstaparParkingSystem.parkingSystem.application.service;

import com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto.WebhookEventRequestDTO;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.enums.EventTypeEnum;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.exception.InvalidRequestException;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.exception.OccupiedParkingSpotException;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.exception.ParkingSpotNotFoundException;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.exception.VehicleNotFoundException;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.model.Garage;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.model.ParkingEvent;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.model.ParkingSpot;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.repository.GarageRepository;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.repository.ParkingEventRepository;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.repository.ParkingSpotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebhookParkedServiceTest {

    @Mock
    private ParkingEventRepository parkingEventRepository;

    @Mock
    private ParkingSpotRepository parkingSpotRepository;

    @Mock
    private GarageRepository garageRepository;

    @InjectMocks
    private WebhookParkedService service;

    private WebhookEventRequestDTO requestDTO;
    private ParkingEvent parkingEvent;
    private ParkingSpot spot;
    private Garage garage;

    @BeforeEach
    void setUp() {
        requestDTO = new WebhookEventRequestDTO(
                "ABC1234",
                null,
                null,
                null,
                -23.0,
                -43.0
        );

        garage = new Garage();
        garage.setSector("A");
        garage.setCurrentOccupancy(0);
        garage.setMaxCapacity(100);

        spot = new ParkingSpot();
        spot.setId(1L);
        spot.setSector("A");
        spot.setGarage(garage);
        spot.setOccupied(false);
        spot.setLatitude(-23.0);
        spot.setLongitude(-43.0);

        parkingEvent = new ParkingEvent();
        parkingEvent.setLicensePlate("ABC1234");
    }

    @Test
    @DisplayName("Should throw InvalidRequestException when latitude or longitude is null")
    void WebhookParkedServiceTest_handleParked_case01() {
        // Arrange
        WebhookEventRequestDTO invalidRequest = new WebhookEventRequestDTO(
                "ABC1234", null, null, null, -43.0, null
        );

        // Act + Assert
        assertThrows(InvalidRequestException.class, () -> service.handleParked(invalidRequest));
    }

    @Test
    @DisplayName("Should throw VehicleNotFoundException when parking event is not found")
    void WebhookParkedServiceTest_handleParked_case02() {
        // Arrange
        when(parkingEventRepository.findTopByLicensePlateAndExitTimeIsNullOrderByEntryTimeDesc(anyString()))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(VehicleNotFoundException.class, () -> service.handleParked(requestDTO));
    }

    @Test
    @DisplayName("Should throw ParkingSpotNotFoundException when parking spot is not found")
    void WebhookParkedServiceTest_handleParked_case03() {
        // Arrange
        when(parkingEventRepository.findTopByLicensePlateAndExitTimeIsNullOrderByEntryTimeDesc(anyString()))
                .thenReturn(Optional.of(parkingEvent));

        when(parkingSpotRepository.findByLatitudeAndLongitude(anyDouble(), anyDouble()))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ParkingSpotNotFoundException.class, () -> service.handleParked(requestDTO));
    }

    @Test
    @DisplayName("Should throw OccupiedParkingSpotException when parking spot is already occupied")
    void WebhookParkedServiceTest_handleParked_case04() {
        // Arrange
        spot.setOccupied(true);

        when(parkingEventRepository.findTopByLicensePlateAndExitTimeIsNullOrderByEntryTimeDesc(anyString()))
                .thenReturn(Optional.of(parkingEvent));

        when(parkingSpotRepository.findByLatitudeAndLongitude(anyDouble(), anyDouble()))
                .thenReturn(Optional.of(spot));

        // Act + Assert
        assertThrows(OccupiedParkingSpotException.class, () -> service.handleParked(requestDTO));
    }

    @Test
    @DisplayName("Should park vehicle successfully and update parking event, spot and garage")
    void WebhookParkedServiceTest_handleParked_case05() {
        // Arrange
        when(parkingEventRepository.findTopByLicensePlateAndExitTimeIsNullOrderByEntryTimeDesc(anyString()))
                .thenReturn(Optional.of(parkingEvent));

        when(parkingSpotRepository.findByLatitudeAndLongitude(anyDouble(), anyDouble()))
                .thenReturn(Optional.of(spot));

        when(garageRepository.save(any())).thenReturn(garage);
        when(parkingEventRepository.save(any())).thenReturn(parkingEvent);
        when(parkingSpotRepository.save(any())).thenReturn(spot);

        // Act
        service.handleParked(requestDTO);

        // Assert
        assertTrue(spot.isOccupied());
        assertEquals(parkingEvent.getLicensePlate(), spot.getCurrentLicensePlate());
        assertEquals(EventTypeEnum.PARKED, parkingEvent.getEventType());
        assertEquals(1, garage.getCurrentOccupancy());

        verify(parkingEventRepository).save(parkingEvent);
        verify(parkingSpotRepository).save(spot);
        verify(garageRepository).save(garage);
    }
}