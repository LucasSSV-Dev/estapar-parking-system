package com.Estapar.EstaparParkingSystem.parkingSystem.domain.exception;

public class GarageNotFoundException extends RuntimeException {
    public GarageNotFoundException(String message) {
        super(message);
    }
}
