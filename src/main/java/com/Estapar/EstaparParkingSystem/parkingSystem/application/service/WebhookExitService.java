package com.Estapar.EstaparParkingSystem.parkingSystem.application.service;

import com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto.RevenueRequestDTO;
import com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto.WebhookEventRequestDTO;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.enums.EventTypeEnum;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.exception.GarageNotFoundException;
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
            throw new InvalidRequestException("Exit time is required for EXIT event");
        }

        ParkingEvent parkingEvent = parkingEventRepository
                .findTopByLicensePlateAndExitTimeIsNullOrderByEntryTimeDesc(requestDTO.licensePlate())
                .orElseThrow(() -> new VehicleNotFoundException(requestDTO.licensePlate()));

        parkingEvent.setExitTime(requestDTO.exitTime());
        parkingEvent.setEventType(EventTypeEnum.EXIT);

        BigDecimal finalPrice = calculateParkingFee(parkingEvent);
        parkingEvent.setPaydPrice(finalPrice);

        parkingEventRepository.save(parkingEvent);

        log.info("Final price: {}", finalPrice);
        log.info("ParkingEvent Exited: {}", parkingEvent);

        RevenueRequestDTO revenueRequestDTO = new RevenueRequestDTO(
                parkingEvent.getExitTime().toLocalDate(),
                parkingEvent.getSector()
        );

        BigDecimal amount = revenueService.getRevenueAmount(revenueRequestDTO);
        Revenue revenue = revenueService.createOrUpdateRevenue(revenueRequestDTO, amount);
        revenueService.saveRevenue(revenue);

        log.info("Revenue saved: {}", revenue);

        ParkingSpot spot = parkingSpotRepository
                .findByCurrentLicensePlate(requestDTO.licensePlate())
                .orElseThrow(() -> new ParkingSpotNotFoundException("Parking spot not found"));

        spot.setOccupied(false);
        spot.setCurrentLicensePlate(null);

        parkingSpotRepository.save(spot);

        log.info("Parking spot released: {}", spot);

        Garage garage = spot.getGarage();

        if (garage == null) {
            garage = garageRepository
                    .findBySector(spot.getSector())
                    .orElseThrow(() -> new GarageNotFoundException(requestDTO.licensePlate()));
            spot.setGarage(garage);
        }

        garage.decrementOccupancy();
        garageRepository.save(garage);
        garageRepository.flush();

        log.info("Garage occupancy decremented: : {} of {}", garage.getCurrentOccupancy(), garage.getMaxCapacity());
        log.info("[ends] WebhookExitService - handleExit()");
    }

    private @NonNull BigDecimal calculateParkingFee(ParkingEvent parkingEvent) {

        BigDecimal price = BigDecimal.ZERO;

        long minutes = Duration.between(
                parkingEvent.getEntryTime(),
                parkingEvent.getExitTime()
        ).toMinutes();

        if (minutes >= 31) {

            long hoursCharged = (long) Math.ceil(minutes / 60.0);

            Garage garage = garageRepository
                    .findBySector(parkingEvent.getSector())
                    .orElseThrow(() -> new GarageNotFoundException("Garage not found"));

            price = garage.getBasePrice()
                    .multiply(BigDecimal.valueOf(hoursCharged))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        return price;
    }
}
