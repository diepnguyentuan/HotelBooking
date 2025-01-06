package org.hotel.repository;
import org.hotel.model.Booking;
import java.time.LocalDateTime;
import java.util.List;


public interface BookingRepository {
    void save(Booking booking);
    void delete(Booking booking);
    void deleteAll();
    Booking findBookingBy(int customerId, int roomId, LocalDateTime checkinTime, LocalDateTime checkoutTime);
    List<Booking> findByCustomerId(int customerId);
    List<Booking> findByRoomId(int roomId);

}