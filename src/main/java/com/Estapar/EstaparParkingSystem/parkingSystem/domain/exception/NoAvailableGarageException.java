package com.Estapar.EstaparParkingSystem.parkingSystem.domain.exception;

public class NoAvailableGarageException extends RuntimeException {
    public NoAvailableGarageException(String message) {
        super(message);
    }
}
