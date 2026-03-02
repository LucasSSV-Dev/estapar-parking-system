package com.Estapar.EstaparParkingSystem.parkingEvent.application.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;

public interface ParkingEventAPI {

    ResponseEntity<URI> createParkingEvent(@RequestBody String licensePlate);

    ResponseEntity<URI> exitParkingEvent(@RequestParam String licensePlate);

}
