package com.merigasparyan.jmp.parkingserviceapi.exception;

public class BookingConflictException extends RuntimeException {
    public BookingConflictException(String message) {
        super(message);
    }
}
