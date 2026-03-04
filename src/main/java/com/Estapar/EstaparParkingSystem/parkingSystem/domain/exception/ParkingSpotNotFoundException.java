package com.Estapar.EstaparParkingSystem.parkingSystem.domain.exception;

public class ParkingSpotNotFoundException extends RuntimeException {
    public ParkingSpotNotFoundException(String message) {
        super(message);
    }
}
