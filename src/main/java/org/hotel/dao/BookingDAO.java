package org.hotel.dao;

import org.hotel.config.DatabaseConfig;
import org.hotel.model.Booking;
import org.hotel.model.Customer;
import org.hotel.model.Room;
import org.hotel.repository.BookingRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO implements BookingRepository {

    public void save(Booking booking) {
        String sql = "INSERT INTO booking (booking_id, customer_id, room_id, checkin_time, checkout_time) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, booking.getBookingId());
            statement.setInt(2, booking.getCustomer().getId());
            statement.setInt(3, booking.getRoom().getRoomNo());
            statement.setTimestamp(4, Timestamp.valueOf(booking.getCheckinTime()));
            statement.setTimestamp(5, Timestamp.valueOf(booking.getCheckoutTime()));
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isRoomAvailable(int roomId, LocalDateTime checkin, LocalDateTime checkout) {
        String sql = "SELECT COUNT(*) FROM booking WHERE room_id = ? AND checkin_time < ? AND checkout_time > ?";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, roomId);
            statement.setTimestamp(2, Timestamp.valueOf(checkout));
            statement.setTimestamp(3, Timestamp.valueOf(checkin));
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) == 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Booking> findByCustomerId(int customerId) {
        String sql = "SELECT * FROM booking WHERE customer_id = ?";
        List<Booking> bookings = new ArrayList<>();
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, customerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Booking booking = new Booking(
                            resultSet.getInt("booking_id"),
                            null,
                            null,
                            resultSet.getTimestamp("checkin_time").toLocalDateTime(),
                            resultSet.getTimestamp("checkout_time").toLocalDateTime()
                    );
                    CustomerDAO customerDAO = new CustomerDAO();
                    RoomDAO roomDAO = new RoomDAO();
                    Customer customer = customerDAO.getById(customerId);
                    Room room = roomDAO.getById(resultSet.getInt("room_id"));
                    booking.setCustomer(customer);
                    booking.setRoom(room);
                    bookings.add(booking);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    @Override
    public void delete(Booking booking) {
        String deleteSql = "DELETE FROM booking WHERE booking_id = ?";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement deleteStatement = connection.prepareStatement(deleteSql)) {
            deleteStatement.setInt(1, booking.getBookingId());
            deleteStatement.executeUpdate();
            System.out.println("Booking with id " + booking.getBookingId() + " has been checked out successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasBooking(int customerId) {
        String sql = "SELECT COUNT(*) FROM booking WHERE customer_id = ?";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, customerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}