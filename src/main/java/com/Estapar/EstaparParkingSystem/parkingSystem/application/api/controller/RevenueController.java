package com.Estapar.EstaparParkingSystem.parkingSystem.application.api.controller;

import com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto.RevenueRequestDTO;
import com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto.RevenueResponseDTO;
import com.Estapar.EstaparParkingSystem.parkingSystem.application.service.RevenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/revenue")
@RequiredArgsConstructor
public class RevenueController {

    private final RevenueService revenueService;

    @GetMapping
    public ResponseEntity<RevenueResponseDTO> getRevenue(
            @RequestBody RevenueRequestDTO requestDTO
    ) {
        RevenueResponseDTO responseDTO = revenueService.calculateRevenue(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }
}
