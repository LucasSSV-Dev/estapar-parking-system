package com.Estapar.EstaparParkingSystem.parkingSystem.domain.exception;

public class NoParkingSpotsAvailableException extends RuntimeException {
    public NoParkingSpotsAvailableException(String message) {
        super(message);
    }
}
