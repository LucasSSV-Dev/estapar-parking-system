package com.Estapar.EstaparParkingSystem.parkingSystem.domain.exception;

public class SectorFullException extends RuntimeException {
    public SectorFullException(String sector) {
        super("Sector " + sector + " is full");
    }
}
