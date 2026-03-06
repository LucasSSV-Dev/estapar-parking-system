package com.Estapar.EstaparParkingSystem.parkingSystem.application.service;

import com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto.WebhookEventRequestDTO;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.enums.EventTypeEnum;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.exception.InvalidRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class WebhookServiceTest {

    @Mock
    private WebhookEntryService entryService;

    @Mock
    private WebhookParkedService parkedService;

    @Mock
    private WebhookExitService exitService;

    @InjectMocks
    private WebhookService webhookService;

    @Test
    @DisplayName("Should delegate to EntryService when event type is ENTRY")
    void shouldCallEntryServiceWhenEventTypeIsEntry() {

        // Arrange
        LocalDateTime entryTime = LocalDateTime.of(2026, 3, 6, 10, 0);
        WebhookEventRequestDTO request =
                new WebhookEventRequestDTO(
                        "ABC123",
                        EventTypeEnum.ENTRY,
                        entryTime,
                        null,
                        null,
                        null );

        // Act
        webhookService.process(request);

        // Assert
        verify(entryService).handleEntry(request);
        verifyNoInteractions(parkedService, exitService);
    }

    @Test
    @DisplayName("Should delegate to ParkedService when event type is PARKED")
    void shouldCallParkedServiceWhenEventTypeIsParked() {

        // Arrange
        LocalDateTime entryTime = LocalDateTime.of(2026, 3, 6, 10, 0);

        WebhookEventRequestDTO request =
                new WebhookEventRequestDTO(
                        "ABC123",
                        EventTypeEnum.PARKED,
                        entryTime,
                        null,
                        -22.9,
                        -43.2 );

        // Act
        webhookService.process(request);

        // Assert
        verify(parkedService).handleParked(request);
        verifyNoInteractions(entryService, exitService);
    }

    @Test
    @DisplayName("Should delegate to ExitService when event type is EXIT")
    void shouldCallExitServiceWhenEventTypeIsExit() {

        // Arrange
        LocalDateTime entryTime = LocalDateTime.of(2026, 3, 6, 10, 0);
        LocalDateTime exitTime = LocalDateTime.of(2026, 3, 6, 11, 0);

        WebhookEventRequestDTO request =
                new WebhookEventRequestDTO(
                        "ABC123",
                        EventTypeEnum.EXIT,
                        entryTime,
                        exitTime,
                        null,
                        null );

        // Act
        webhookService.process(request);

        // Assert
        verify(exitService).handleExit(request);
        verifyNoInteractions(entryService, parkedService);
    }

    @Test
    @DisplayName("Should throw InvalidRequestException when event type is invalid")
    void shouldThrowExceptionWhenEventTypeIsInvalid() {

        // Arrange
        WebhookEventRequestDTO request =
                new WebhookEventRequestDTO(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null );

        // Act & Assert
        assertThrows(
                InvalidRequestException.class,
                () -> webhookService.process(request)
        );

        verifyNoInteractions(entryService, parkedService, exitService);
    }

}