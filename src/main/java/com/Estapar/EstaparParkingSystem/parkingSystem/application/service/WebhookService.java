package com.Estapar.EstaparParkingSystem.parkingSystem.application.service;

import com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto.WebhookEventRequestDTO;

public class WebhookService {

    public void process(WebhookEventRequestDTO request) {

        switch (request.eventType()) {
            case ENTRY -> handleEntry(request);
            case PARKED -> handleParked(request);
            case EXIT -> handleExit(request);
            default -> throw new IllegalArgumentException("Unknown event type");
        }
    }

    //Resto do código vai vir aqui embaixo

    private void handleEntry(WebhookEventRequestDTO request) {
    }

    private void handleParked(WebhookEventRequestDTO request) {
    }

    private void handleExit(WebhookEventRequestDTO request) {
        
    }









}
