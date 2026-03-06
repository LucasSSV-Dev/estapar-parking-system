package com.Estapar.EstaparParkingSystem.parkingSystem.application.service;

import com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto.WebhookEventRequestDTO;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.enums.EventTypeEnum;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.exception.InvalidRequestException;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.model.ParkingEvent;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.repository.ParkingEventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WebhookEntryServiceTest {

    @Mock
    private ParkingEventRepository parkingEventRepository;

    @InjectMocks
    private WebhookEntryService webhookEntryService;

    @Test
    @DisplayName("Should save ParkingEvent correctly when entryTime is valid")
    void WebhookEntryServiceTest_handleEntry_shouldSaveParkingEventWithCorrectData() {
        // Arrange
        LocalDateTime entryTime = LocalDateTime.now();

        WebhookEventRequestDTO request = new WebhookEventRequestDTO(
                "ABC1234",
                EventTypeEnum.ENTRY,
                entryTime,
                null,
                null,
                null
        );

        ArgumentCaptor<ParkingEvent> captor = ArgumentCaptor.forClass(ParkingEvent.class);

        // Act
        webhookEntryService.handleEntry(request);

        // Assert
        verify(parkingEventRepository).save(captor.capture());

        ParkingEvent savedEvent = captor.getValue();

        assertEquals("ABC1234", savedEvent.getLicensePlate());
        assertEquals(EventTypeEnum.ENTRY, savedEvent.getEventType());
        assertEquals(entryTime, savedEvent.getEntryTime());
    }

    @Test
    @DisplayName("Should throw InvalidRequestException when entryTime is null")
    void WebhookEntryServiceTest_handleEntry_shouldThrowExceptionWhenEntryTimeIsNull() {

        // Arrange
        LocalDateTime entryTime = null;
        WebhookEventRequestDTO request = new WebhookEventRequestDTO(

                "ABC1234",
                EventTypeEnum.ENTRY,
                entryTime,
                null,
                null,
                null
        );

        // Act + Assert
        assertThrows(
                InvalidRequestException.class,
                () -> webhookEntryService.handleEntry(request)
        );

        verify(parkingEventRepository, never()).save(new ParkingEvent());
    }

}