package com.merigasparyan.jmp.parkingserviceapi.enums;

public enum BookingStatus {
    RESERVED,    // Created and confirmed — active but not yet in use (car not arrived yet)
    PARKED,      // Vehicle is parked in the spot
    VACATED,     // Vehicle has left, spot still associated until end of booking
    CANCELLED,   // Booking cancelled by user/admin before usage
    COMPLETED    // Booking fully completed
}
