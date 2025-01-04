package org.hotel.repository;

import org.hotel.model.Booking;

import java.util.List;

public interface BookingRepository {
    void save (Booking booking);
    List<Booking> findByCustomerId(int customerId);
    void delete(Booking booking);
    boolean hasBooking(int customerId);
}
