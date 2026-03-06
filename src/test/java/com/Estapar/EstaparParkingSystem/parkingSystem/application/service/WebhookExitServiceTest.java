package com.Estapar.EstaparParkingSystem.parkingSystem.application.service;

import com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto.WebhookEventRequestDTO;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.enums.EventTypeEnum;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.exception.InvalidExitException;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebhookExitServiceTest {

    @Mock
    private ParkingEventRepository parkingEventRepository;

    @Mock
    private ParkingSpotRepository parkingSpotRepository;

    @Mock
    private GarageRepository garageRepository;

    @InjectMocks
    private WebhookExitService service;

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
                LocalDateTime.now(),
                null,
                null
        );

        garage = new Garage();
        garage.setSector("A");
        garage.setBasePrice(BigDecimal.valueOf(10));
        garage.setMaxCapacity(100);
        garage.setCurrentOccupancy(10);

        spot = new ParkingSpot();
        spot.setId(1L);
        spot.setSector("A");
        spot.setGarage(garage);
        spot.setOccupied(true);
        spot.setCurrentLicensePlate("ABC1234");

        parkingEvent = new ParkingEvent();
        parkingEvent.setLicensePlate("ABC1234");
        parkingEvent.setEntryTime(LocalDateTime.now().minusHours(2));
        parkingEvent.setSector("A");
    }

    @Test
    @DisplayName("Should throw InvalidExitException when exitTime is null")
    void WebhookExitServiceTest_handleExit_case01() {

        // Arrange
        WebhookEventRequestDTO invalidRequest = new WebhookEventRequestDTO(
                "ABC1234",
                null,
                null,
                null,
                null,
                null
        );

        // Assert
        assertThrows(InvalidExitException.class,
                () -> service.handleExit(invalidRequest));
    }

    @Test
    @DisplayName("Should throw VehicleNotFoundException when parking event is not found")
    void WebhookExitServiceTest_handleExit_case02() {

        // Arrange
        when(parkingEventRepository
                .findTopByLicensePlateAndExitTimeIsNullOrderByEntryTimeDesc(anyString()))
                .thenReturn(Optional.empty());

        // Assert
        assertThrows(VehicleNotFoundException.class,
                () -> service.handleExit(requestDTO));
    }

    @Test
    @DisplayName("Should throw ParkingSpotNotFoundException when parking spot is not found")
    void WebhookExitServiceTest_handleExit_case03() {

        // Arrange
        when(parkingEventRepository
                .findTopByLicensePlateAndExitTimeIsNullOrderByEntryTimeDesc(anyString()))
                .thenReturn(Optional.of(parkingEvent));

        when(parkingSpotRepository
                .findByCurrentLicensePlate(anyString()))
                .thenReturn(Optional.empty());

        // Assert
        assertThrows(ParkingSpotNotFoundException.class,
                () -> service.handleExit(requestDTO));
    }

    @Test
    @DisplayName("Should process exit successfully and update parking event, spot and garage")
    void WebhookExitServiceTest_handleExit_case04() {

        // Arrange
        when(parkingEventRepository
                .findTopByLicensePlateAndExitTimeIsNullOrderByEntryTimeDesc(anyString()))
                .thenReturn(Optional.of(parkingEvent));

        when(parkingSpotRepository
                .findByCurrentLicensePlate(anyString()))
                .thenReturn(Optional.of(spot));

        when(parkingEventRepository.save(any())).thenReturn(parkingEvent);
        when(parkingSpotRepository.save(any())).thenReturn(spot);
        when(garageRepository.save(any())).thenReturn(garage);

        // Act
        service.handleExit(requestDTO);

        // Assert
        assertFalse(spot.isOccupied());
        assertNull(spot.getCurrentLicensePlate());
        assertEquals(EventTypeEnum.EXIT, parkingEvent.getEventType());
        assertNotNull(parkingEvent.getExitTime());
        assertEquals(9, garage.getCurrentOccupancy());

        verify(parkingEventRepository).save(parkingEvent);
        verify(parkingSpotRepository).save(spot);
        verify(garageRepository).save(garage);
    }

    @Test
    @DisplayName("Should not charge when parked less than or equal to 30 minutes")
    void WebhookExitServiceTest_handleExit_case05() {

        // Arrange
        parkingEvent.setEntryTime(LocalDateTime.now().minusMinutes(20));

        when(parkingEventRepository
                .findTopByLicensePlateAndExitTimeIsNullOrderByEntryTimeDesc(anyString()))
                .thenReturn(Optional.of(parkingEvent));

        when(parkingSpotRepository
                .findByCurrentLicensePlate(anyString()))
                .thenReturn(Optional.of(spot));

        // Act
        service.handleExit(requestDTO);

        // Assert
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), parkingEvent.getPaidPrice());
    }

    @Test
    @DisplayName("Should calculate parking fee correctly when parked more than one hour")
    void WebhookExitServiceTest_handleExit_case06() {

        // Arrange
        parkingEvent.setEntryTime(LocalDateTime.now().minusHours(2).minusMinutes(10));

        when(parkingEventRepository
                .findTopByLicensePlateAndExitTimeIsNullOrderByEntryTimeDesc(anyString()))
                .thenReturn(Optional.of(parkingEvent));

        when(parkingSpotRepository
                .findByCurrentLicensePlate(anyString()))
                .thenReturn(Optional.of(spot));

        // Act
        service.handleExit(requestDTO);

        // Assert
        assertEquals(new BigDecimal("30.00"), parkingEvent.getPaidPrice());
    }
}