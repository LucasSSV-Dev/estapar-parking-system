package com.Estapar.EstaparParkingSystem.parkingSystem.domain.exception;

public class ParkingEventAlreadyClosedException extends RuntimeException {
    public ParkingEventAlreadyClosedException(String message) {
        super(message);
    }
}
