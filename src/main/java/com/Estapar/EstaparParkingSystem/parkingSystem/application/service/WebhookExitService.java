package com.Estapar.EstaparParkingSystem.parkingSystem.application.service;

import com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto.RevenueRequestDTO;
import com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto.WebhookEventRequestDTO;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.enums.EventTypeEnum;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.exception.InvalidExitException;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.exception.InvalidRequestException;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.exception.ParkingSpotNotFoundException;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.exception.VehicleNotFoundException;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.model.Garage;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.model.ParkingEvent;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.model.ParkingSpot;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.model.Revenue;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.repository.GarageRepository;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.repository.ParkingEventRepository;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.repository.ParkingSpotRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;

@Service
@Log4j2
@RequiredArgsConstructor
public class WebhookExitService {

    private final ParkingEventRepository parkingEventRepository;
    private final ParkingSpotRepository parkingSpotRepository;
    private final GarageRepository garageRepository;
    private final RevenueService revenueService;

    public void handleExit(WebhookEventRequestDTO requestDTO) {
        log.info("[starts] WebhookExitService - handleExit()");

        if (requestDTO.exitTime() == null) {
            throw new InvalidExitException("Exit time is required for EXIT event");
        }

        //Encontra o carro.
        ParkingEvent parkingEvent = parkingEventRepository
                .findTopByLicensePlateAndExitTimeIsNullOrderByEntryTimeDesc(requestDTO.licensePlate())
                .orElseThrow(() -> new VehicleNotFoundException(requestDTO.licensePlate()));
        //Encontra a vaga.
        ParkingSpot spot = parkingSpotRepository
                .findByCurrentLicensePlate(requestDTO.licensePlate())
                .orElseThrow(() -> new ParkingSpotNotFoundException("Parking spot not found"));
        //Encontra a garagem.
        Garage garage = spot.getGarage();

        parkingEvent.setExitTime(requestDTO.exitTime());
        parkingEvent.setEventType(EventTypeEnum.EXIT);

        if (parkingEvent.getSector() != null) {
            //Esvazia a vaga
            spot.setOccupied(false);
            spot.setCurrentLicensePlate(null);
            log.info("Plate {} exited from parking spot {} from sector {} ", parkingEvent.getLicensePlate(), spot.getId(), spot.getSector());

            //Diminui a currentOccupancy da garagem
            garage.decrementOccupancy();

            //Calcula o valor pago na saída
            BigDecimal finalPrice = calculateParkingFee(parkingEvent, garage);
            parkingEvent.setPaidPrice(finalPrice);

            log.info("Final price: {}", finalPrice);

            RevenueRequestDTO revenueRequestDTO = new RevenueRequestDTO(
                    parkingEvent.getExitTime().toLocalDate(),
                    parkingEvent.getSector()
            );
            BigDecimal amount = revenueService.getRevenueAmount(revenueRequestDTO);
            Revenue revenue = revenueService.createOrUpdateRevenue(revenueRequestDTO, amount);

            revenueService.save(revenue);

            log.info("Revenue from sector {} saved. Amount: {}", revenue.getSector(), revenue.getAmount());
            log.info("Garage {} occupancy decremented: {} of {}", garage.getSector(), garage.getCurrentOccupancy(), garage.getMaxCapacity());
        }
        parkingEventRepository.save(parkingEvent);
        parkingSpotRepository.save(spot);
        garageRepository.save(garage);
        log.info("[ends] WebhookExitService - handleExit()");
    }

    private @NonNull BigDecimal calculateParkingFee(ParkingEvent parkingEvent, Garage garage) {
        BigDecimal price = BigDecimal.ZERO;

        long minutes = Duration.between(
                parkingEvent.getEntryTime(),
                parkingEvent.getExitTime()
        ).toMinutes();

        if (minutes >= 31) {

            long hoursCharged = (long) Math.ceil(minutes / 60.0);

            price = garage.getBasePrice()
                    .multiply(BigDecimal.valueOf(hoursCharged))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        return price;
    }
}
