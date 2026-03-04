package com.Estapar.EstaparParkingSystem.parkingSystem.domain.exception;

public class ParkingEventNotOpenException extends RuntimeException {
    public ParkingEventNotOpenException(String message) {
        super(message);
    }
}
