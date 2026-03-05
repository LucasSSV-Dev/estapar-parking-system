package com.Estapar.EstaparParkingSystem.parkingSystem.application.service;

import com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto.IntervalDTO;
import com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto.RevenueRequestDTO;
import com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto.RevenueResponseDTO;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.enums.EventTypeEnum;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.model.Revenue;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.repository.ParkingEventRepository;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.repository.RevenueRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class RevenueService {

    private final ParkingEventRepository parkingEventRepository;
    private final RevenueRepository revenueRepository;

    @Transactional
    public RevenueResponseDTO calculateRevenue(RevenueRequestDTO revenueRequestDTO) {
        log.info("[starts] RevenueService - calculateRevenue()");

        //Calculo no banco de dados
        BigDecimal amount = getRevenueAmount(revenueRequestDTO);

        //Vejo se já foi realizado o cálculo para essa data e setor pra não criar múltiplas receitas do mesmo período
        Revenue revenue = createOrUpdateRevenue(revenueRequestDTO, amount);

        //Salva no banco de dados
        saveRevenue(revenue);

        log.info("[ends] RevenueService - calculateRevenue()\n");
        return revenue.toResponseDTO();
    }

    protected void saveRevenue(Revenue revenue) {
        revenueRepository.save(revenue);
    }

    protected Revenue createOrUpdateRevenue(RevenueRequestDTO revenueRequestDTO, BigDecimal amount) {
         Optional<Revenue> revenue = revenueRepository.findBySectorAndDate(
                revenueRequestDTO.sector(),
                revenueRequestDTO.date()
        );
        return revenue.orElseGet(() -> new Revenue(revenueRequestDTO.sector(), revenueRequestDTO.date(), amount));
    }

    protected BigDecimal getRevenueAmount(RevenueRequestDTO revenueRequestDTO) {
        IntervalDTO interval = getDateStartAndEnd(revenueRequestDTO);
        return parkingEventRepository
                .sumRevenueByDateAndSectorAndEventType(
                        interval.start(),
                        interval.end(),
                        revenueRequestDTO.sector(),
                        EventTypeEnum.EXIT
                ).orElse(BigDecimal.ZERO).setScale(2, RoundingMode.DOWN);
    }

    private IntervalDTO getDateStartAndEnd(RevenueRequestDTO dto) {
        LocalDateTime start = dto.date().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        return new IntervalDTO(start, end);
    }
}
