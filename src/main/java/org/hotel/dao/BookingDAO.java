package org.hotel.dao;
import org.hotel.config.DatabaseConfig;
import org.hotel.model.Booking;
import org.hotel.model.Customer;
import org.hotel.model.Room;
import org.hotel.repository.BookingRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO implements BookingRepository {


    @Override
    public void save(Booking booking) {
        String sql = "INSERT INTO booking (customer_id, room_id, checkin_time, checkout_time) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, booking.getCustomer().getId());
            preparedStatement.setInt(2, booking.getRoom().getRoomNo());
            preparedStatement.setObject(3, booking.getCheckinTime());
            preparedStatement.setObject(4, booking.getCheckoutTime());
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating booking failed, no rows affected.");
            }
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int bookingId = generatedKeys.getInt(1);
                    booking.setBookingId(bookingId);
                }
                else {
                    throw new SQLException("Creating booking failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public Booking findBookingBy(int customerId, int roomId, LocalDateTime checkinTime, LocalDateTime checkoutTime){
        String sql = "SELECT booking_id, customer_id, room_id, checkin_time, checkout_time FROM booking WHERE customer_id = ? AND room_id = ? AND checkin_time = ? AND checkout_time = ?";
        try(Connection connection = DatabaseConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql))
        {
            preparedStatement.setInt(1, customerId);
            preparedStatement.setInt(2, roomId);
            preparedStatement.setObject(3, checkinTime);
            preparedStatement.setObject(4, checkoutTime);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                int bookingId = resultSet.getInt("booking_id");
                int customerIdDB = resultSet.getInt("customer_id");
                int roomIdDB = resultSet.getInt("room_id");
                LocalDateTime checkinTimeDB = resultSet.getObject("checkin_time", LocalDateTime.class);
                LocalDateTime checkoutTimeDB = resultSet.getObject("checkout_time", LocalDateTime.class);
                Customer customer = new Customer();
                customer.setId(customerIdDB);
                Room room = new Room();
                room.setRoomNo(roomIdDB);
                Booking booking = new Booking(bookingId, customer, room, checkinTimeDB, checkoutTimeDB);
                return booking;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Booking> findByCustomerId(int customerId) {
        String sql = "SELECT b.booking_id, b.customer_id, b.room_id, b.checkin_time, b.checkout_time, " +
                "r.room_no, r.type_room, r.price " +
                "FROM booking b " +
                "INNER JOIN rooms r ON b.room_id = r.room_no " +
                "WHERE b.customer_id = ?";
        List<Booking> bookings = new ArrayList<>();
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, customerId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int bookingId = resultSet.getInt("booking_id");
                int customerIdDB = resultSet.getInt("customer_id");
                int roomNoDB = resultSet.getInt("room_no");
                String typeRoom = resultSet.getString("type_room");
                double price = resultSet.getDouble("price");
                LocalDateTime checkinTimeDB = resultSet.getObject("checkin_time", LocalDateTime.class);
                LocalDateTime checkoutTimeDB = resultSet.getObject("checkout_time", LocalDateTime.class);


                Customer customer = new Customer();
                customer.setId(customerIdDB);
                Room room = new Room(roomNoDB, typeRoom, price, true);
                Booking booking = new Booking(bookingId, customer, room, checkinTimeDB, checkoutTimeDB);
                bookings.add(booking);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }
    @Override
    public List<Booking> findByRoomId(int roomId){
        String sql = "SELECT booking_id, customer_id, room_id, checkin_time, checkout_time FROM booking WHERE room_id = ?";
        List<Booking> bookings = new ArrayList<>();
        try(Connection connection = DatabaseConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql))
        {
            preparedStatement.setInt(1, roomId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                int bookingId = resultSet.getInt("booking_id");
                int customerIdDB = resultSet.getInt("customer_id");
                int roomIdDB = resultSet.getInt("room_id");
                LocalDateTime checkinTimeDB = resultSet.getObject("checkin_time", LocalDateTime.class);
                LocalDateTime checkoutTimeDB = resultSet.getObject("checkout_time", LocalDateTime.class);
                Customer customer = new Customer();
                customer.setId(customerIdDB);
                Room room = new Room();
                room.setRoomNo(roomIdDB);
                Booking booking = new Booking(bookingId, customer, room, checkinTimeDB, checkoutTimeDB);
                bookings.add(booking);
            }
        }
        catch (SQLException e) {
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
    public void deleteAll() {
        String deleteSql = "DELETE FROM booking";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement deleteStatement = connection.prepareStatement(deleteSql)) {
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}