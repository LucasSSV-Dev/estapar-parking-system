package com.Estapar.EstaparParkingSystem.parkingSystem.application.api.controller;

import com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto.WebhookEventRequestDTO;
import com.Estapar.EstaparParkingSystem.parkingSystem.application.service.WebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/webhook")
public class WebhookController {

    private final WebhookService webhookService;

    @PostMapping
    public ResponseEntity<Void> handleEvent(@RequestBody WebhookEventRequestDTO eventRequestDTO) {
        log.info("[starts] WebhookController - handleEvent()");
        webhookService.process(eventRequestDTO);
        log.info("[ends] WebhookController - handleEvent()\n");
        return ResponseEntity.ok().build(); //Famoso 200 OK
    }
}
