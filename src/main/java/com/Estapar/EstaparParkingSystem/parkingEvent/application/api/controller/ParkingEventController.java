package com.Estapar.EstaparParkingSystem.parkingEvent.application.api.controller;

import com.Estapar.EstaparParkingSystem.common.tools.LocationUriBuilder;
import com.Estapar.EstaparParkingSystem.parkingEvent.application.service.ParkingEventService;
import com.Estapar.EstaparParkingSystem.parkingEvent.domain.model.ParkingEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/parking")
public class ParkingEventController implements ParkingEventAPI {

    private final ParkingEventService parkingEventService;
    private final LocationUriBuilder locationUriBuilder;


    @PostMapping("/entry")
    public ResponseEntity<URI> createParkingEvent(@RequestBody String licensePlate) {
        //Entrando :D
        ParkingEvent entryEvent = parkingEventService.createParkingEvent(licensePlate);

        //Ainda não sei se vou precisar
        //Retorna URI do evento criado
        URI location = locationUriBuilder.build(entryEvent.getId());

        //Respodendo created pra
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/exit/{licensePlate}")
    public ResponseEntity<URI> exitParkingEvent(@RequestParam String licensePlate) {
        ParkingEvent parkingEventEntity = parkingEventService.createParkingEvent(licensePlate);

        return ResponseEntity.ok().build();
    }

}
