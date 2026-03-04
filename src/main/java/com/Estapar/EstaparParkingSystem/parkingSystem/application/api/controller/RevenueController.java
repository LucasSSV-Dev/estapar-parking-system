package com.Estapar.EstaparParkingSystem.parkingSystem.application.api.controller;

import com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto.RevenueRequestDTO;
import com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto.RevenueResponseDTO;
import com.Estapar.EstaparParkingSystem.parkingSystem.application.service.RevenueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/revenue")
@RequiredArgsConstructor
@Log4j2
public class RevenueController {

    private final RevenueService revenueService;

    @GetMapping

    public ResponseEntity<RevenueResponseDTO> getRevenue(
            @RequestBody RevenueRequestDTO requestDTO
    ) {
        log.info("[starts] RevenueController - getRevenue()");
        RevenueResponseDTO responseDTO = revenueService.calculateRevenue(requestDTO);
        log.info("[ends] RevenueController - getRevenue()\n");
        return ResponseEntity.ok(responseDTO);
    }
}

