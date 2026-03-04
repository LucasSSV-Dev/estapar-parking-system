package com.Estapar.EstaparParkingSystem.parkingSystem.application.service;

import com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto.WebhookEventRequestDTO;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.enums.EventTypeEnum;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.model.Garage;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.model.ParkingEvent;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.model.ParkingSpot;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.repository.GarageRepository;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.repository.ParkingEventRepository;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.repository.ParkingSpotRepository;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;

@Service
@Log4j2
@RequiredArgsConstructor
public class WebhookService {

    private final GarageRepository garageRepository;
    private final ParkingEventRepository parkingEventRepository;
    private final ParkingSpotRepository parkingSpotRepository;


    @Transactional
    public void process(WebhookEventRequestDTO requestDTO) {
        log.info("[starts] WebhookService - process()");
        switch (requestDTO.eventType()) {
            case ENTRY -> handleEntry(requestDTO);
            case PARKED -> handleParked(requestDTO);
            case EXIT -> handleExit(requestDTO);
            default -> throw new IllegalArgumentException("Unknown event type");
        }
    }

    //Agora vem o código:

    // ENTRY
    private void handleEntry(WebhookEventRequestDTO requestDTO) {
        log.info("[starts] WebhookService - handleEntry()");

        //Só pra ter certeza =D
        if (requestDTO.entryTime() == null) { //Não pude botar esse validator no DTO
            throw new IllegalArgumentException("entryTime is required for ENTRY event");
        }

        //Buscar setor com vaga
        Garage sector = garageRepository
                .findAvailable(PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Garage is full"));

        //Criar evento
        ParkingEvent parkingEvent = new ParkingEvent();
        parkingEvent.setLicensePlate(requestDTO.licensePlate());
        parkingEvent.setEventType(EventTypeEnum.ENTRY);
        parkingEvent.setEntryTime(requestDTO.entryTime());
        parkingEvent.setDynamicPrice(sector.calculateDynamicPrice()); //Calcula e insere o valor do desconto

        //Salvar o ParkingEvent
        parkingEventRepository.save(parkingEvent);

        //Aumenta a currentOccupancy e atualiza no banco de dados
        sector.incrementOccupancy();
        garageRepository.save(sector);
        log.info("[ends] WebhookService - handleEntry()\n");
    }

    //PARKED
    private void handleParked(WebhookEventRequestDTO requestDTO) {
        log.info("[starts] WebhookService - handleParked()");
        //Vou precisar desses dados e não pude botar esse validator no DTO
        if (requestDTO.lat() == null || requestDTO.lng() == null) {
            throw new IllegalArgumentException("latitude and longitude are required for PARKED event");
        }

        //Verifica se já houve ENTRY dessa placa e prepara o parkingEvent pra receber o valor do sector
        ParkingEvent parkingEvent = parkingEventRepository
                .findTopByLicensePlateAndExitTimeIsNullOrderByEntryTimeDesc(requestDTO.licensePlate())
                .orElseThrow(() -> new IllegalStateException("Parking event not found"));

        //Busca vaga pela coordenada
        ParkingSpot spot = parkingSpotRepository
                .findByLatitudeAndLongitude(requestDTO.lat(), requestDTO.lng())
                .orElseThrow(() -> new IllegalStateException("Parking spot not found"));

        //Verifica se já está ocupada
        if (spot.isOccupied()) {
            throw new IllegalStateException("Parking spot already occupied");
        }

        //Ocupa a vaga, insere a LicensePlate e atualiza no banco de dados
        spot.setOccupied(true);
        spot.setCurrentLicensePlate(parkingEvent.getLicensePlate());
        parkingEvent.setSector(spot.getSector());
        parkingSpotRepository.save(spot);
        parkingEventRepository.save(parkingEvent);
        log.info("[ends] WebhookService - handleParked()\n");
    }

    //EXIT
    private void handleExit(WebhookEventRequestDTO requestDTO) {
        log.info("[starts] WebhookService - handleExit()");
        //De novo hahaha É isso, tenho que validar aqui né
        if (requestDTO.exitTime() == null) {
            throw new IllegalArgumentException("exitTime is required for EXIT event");
        }

        // busca evento ativo
        ParkingEvent parkingEvent = parkingEventRepository
                .findTopByLicensePlateAndExitTimeIsNullOrderByEntryTimeDesc(requestDTO.licensePlate())
                .orElseThrow(() -> new IllegalStateException("Vehicle not found in parking"));

        //Registra saída e Calcula valor pago.
        parkingEvent.setExitTime(requestDTO.exitTime());
        parkingEvent.setEventType(EventTypeEnum.EXIT);
        BigDecimal finalPrice = calculateParkingFee(parkingEvent);
        parkingEvent.setPaydPrice(finalPrice);

        parkingEventRepository.save(parkingEvent);

        //Encontra a vaga pelo licensePlate
        ParkingSpot spot = parkingSpotRepository
                .findByCurrentLicensePlate(requestDTO.licensePlate())
                .orElseThrow(() -> new IllegalStateException("Parking spot not found"));

        //liberar vaga
            spot.setOccupied(false);
            spot.setCurrentLicensePlate(null);
            parkingSpotRepository.save(spot);

        //Diminui a currentOccupancy do garage
        Garage garage = spot.getGarage();
        if (garage == null) {
            throw new IllegalStateException("Parking spot without garage");
        }
        garage.decrementOccupancy();
        garageRepository.save(garage);
        log.info("[ends] WebhookService - handleExit()\n");
    }

    private @NonNull BigDecimal calculateParkingFee(ParkingEvent parkingEvent) {
        BigDecimal price = BigDecimal.ZERO;
        //Calcula tempo estacionado
        long minutes = Duration.between(
                parkingEvent.getEntryTime(),
                parkingEvent.getExitTime()
        ).toMinutes();

        //Arredonda pra cima se for maior que 30 minutos
        if (minutes >= 31) {
            long hoursCharged = (long) Math.ceil(minutes / 60.0);
            // Busca garagem pelo sector salvo no evento
            Garage garage = garageRepository
                    .findBySector(parkingEvent.getSector())
                    .orElseThrow(() -> new IllegalStateException("Garage not found"));

            price = garage.getBasePrice()
                    .multiply(BigDecimal.valueOf(hoursCharged))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        return price;
    }

}
