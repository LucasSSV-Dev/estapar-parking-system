package com.Estapar.EstaparParkingSystem.parkingSystem.application.api.controller;

import com.Estapar.EstaparParkingSystem.parkingSystem.application.service.GarageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GarageController {

    private final GarageService garageService;


}
