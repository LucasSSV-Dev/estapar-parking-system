package com.Estapar.EstaparParkingSystem.parkingSystem.domain.exception;

public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message) {
        super(message);
    }
}
