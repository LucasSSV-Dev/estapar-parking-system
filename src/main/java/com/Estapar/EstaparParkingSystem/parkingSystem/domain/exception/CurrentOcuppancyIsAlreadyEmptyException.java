package com.Estapar.EstaparParkingSystem.parkingSystem.domain.exception;

public class CurrentOcuppancyIsAlreadyEmptyException extends RuntimeException {
    public CurrentOcuppancyIsAlreadyEmptyException(String message) {
        super(message);
    }
}
