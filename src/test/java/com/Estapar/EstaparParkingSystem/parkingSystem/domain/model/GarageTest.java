package com.Estapar.EstaparParkingSystem.parkingSystem.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GarageTest {

    @Test
    @DisplayName("Should apply 10% discount when occupancy is less than 25%")
    void GarageTest_calculateDynamicPrice_case01() {

        Garage garage = new Garage();
        garage.setMaxCapacity(100);
        garage.setCurrentOccupancy(10); // 10%

        BigDecimal result = garage.calculateDynamicPrice();

        assertEquals(new BigDecimal("0.90"), result);
    }

    @Test
    @DisplayName("Should apply no discount when occupancy is between 25% and 50%")
    void GarageTest_calculateDynamicPrice_case02() {

        Garage garage = new Garage();
        garage.setMaxCapacity(100);
        garage.setCurrentOccupancy(40); // 40%

        BigDecimal result = garage.calculateDynamicPrice();

        assertEquals(new BigDecimal("1.00"), result);
    }

    @Test
    @DisplayName("Should increase price by 10% when occupancy is between 50% and 75%")
    void GarageTest_calculateDynamicPrice_case03() {

        Garage garage = new Garage();
        garage.setMaxCapacity(100);
        garage.setCurrentOccupancy(60); // 60%

        BigDecimal result = garage.calculateDynamicPrice();

        assertEquals(new BigDecimal("1.10"), result);
    }

    @Test
    @DisplayName("Should increase price by 25% when occupancy is between 75% and 100%")
    void GarageTest_calculateDynamicPrice_case04() {

        Garage garage = new Garage();
        garage.setMaxCapacity(100);
        garage.setCurrentOccupancy(90); // 90%

        BigDecimal result = garage.calculateDynamicPrice();

        assertEquals(new BigDecimal("1.25"), result);
    }
}