package com.Estapar.EstaparParkingSystem.parkingSystem.domain.exception;

public class VehicleNotFoundException extends RuntimeException {
    public VehicleNotFoundException(String plate) {
        super("Vehicle with plate " + plate + " not found in parking");
    }
}
