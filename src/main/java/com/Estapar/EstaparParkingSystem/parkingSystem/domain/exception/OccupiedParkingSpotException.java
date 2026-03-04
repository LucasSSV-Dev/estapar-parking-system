package com.Estapar.EstaparParkingSystem.parkingSystem.domain.exception;

public class OccupiedParkingSpotException extends RuntimeException {
    public OccupiedParkingSpotException(String message) {
        super(message);
    }
}
