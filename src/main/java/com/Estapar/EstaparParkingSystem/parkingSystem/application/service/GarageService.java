package com.Estapar.EstaparParkingSystem.parkingSystem.application.service;

import com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto.GarageConfigResponseDTO;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.model.GarageSector;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GarageService{

    private final GarageRepository garageRepository;
    private final SectorRepository sectorRepository;

    @Transactional
    @Override
    public void importGarageConfig(GarageConfigResponseDTO dto) {

        // evita duplicar se já existir
        if (garageRepository.existsById(1L)) return;

        GarageSector garage = new GarageSector();
        garage.setId(1L);
        garage.setCapacity(dto.getCapacity());
        garageRepository.save(garage);

        List<SectorEntity> sectors = dto.getSectors().stream()
                .map(name -> new SectorEntity(null, name, garage))
                .toList();

        sectorRepository.saveAll(sectors);
    }
}
