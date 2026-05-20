package com.Estapar.EstaparParkingSystem;

import com.Estapar.EstaparParkingSystem.parkingSystem.application.service.GarageImportService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class EstaparParkingSystemApplicationTests {

	@MockBean
	GarageImportService garageImportService;

	@Test
	void contextLoads() {
	}

}
