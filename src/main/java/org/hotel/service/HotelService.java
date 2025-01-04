package org.hotel.service;

import org.hotel.dao.BookingDAO;
import org.hotel.model.Booking;
import org.hotel.model.Customer;
import org.hotel.model.Room;
import org.hotel.repository.BookingRepository;
import org.hotel.repository.CustomerRepository;
import org.hotel.repository.RoomRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HotelService {
    private CustomerRepository customerRepository;
    private RoomRepository roomRepository;
    private BookingRepository bookingRepository;
    private Customer currentCustomer;

    public HotelService(CustomerRepository customerRepository, RoomRepository roomRepository, BookingRepository bookingRepository) {
        this.customerRepository = customerRepository;
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
    }

    public Customer getCurrentCustomer() {
        return currentCustomer;
    }

    public List<Room> getRooms() {
        return roomRepository.getAll();
    }

    public List<Customer> getCustomers() {
        return customerRepository.getAll();
    }

    public void loginCustomer(String name) {
        Customer customer = customerRepository.findByName(name);
        if (customer == null) {
            customer = new Customer(name);
            customerRepository.save(customer);
        }
        currentCustomer = customer;
    }

    public void addRoom(Room room) {
        try {
            roomRepository.save(room);
        } catch (Exception e) {
            System.out.println("Error saving the room.");
            e.printStackTrace();
        }

    }

    public void logout() {
        currentCustomer = null;
    }

    public void bookRoom(int roomNumber, LocalDateTime checkinDateTime, LocalDateTime checkoutDateTime) {
        Room room = findRoom(roomNumber);
        if (room == null) {
            System.out.println("Room " + roomNumber + " does not exists.");
            return;
        }
        if (isRoomAvailable(room, checkinDateTime, checkoutDateTime)) {
            int newId = (int) (Math.random() * 1000);
            Booking booking = new Booking(newId, currentCustomer, room, checkinDateTime, checkoutDateTime);
            try {
                bookingRepository.save(booking);
            } catch (Exception e) {
                System.out.println("Error saving the booking.");
                e.printStackTrace();
                return;
            }
            room.addBookingTime(checkinDateTime, checkoutDateTime);
            room.setAvailable(false);
            try {
                roomRepository.save(room);
            } catch (Exception e) {
                System.out.println("Error saving the room.");
                e.printStackTrace();
                return;
            }

        }
    }

    public void cancelRoom(int roomNumber) {
        Room room = findRoom(roomNumber);
        if (room == null) {
            System.out.println("Room " + roomNumber + " does not exists.");
            return;
        }
        List<Booking> bookings = bookingRepository.findByCustomerId(currentCustomer.getId());
        for (Booking booking : bookings) {
            if (booking.getRoom().getRoomNo() == roomNumber) {
                try {
                    bookingRepository.delete(booking);
                } catch (Exception e) {
                    System.out.println("Error deleting the booking.");
                    e.printStackTrace();
                    return;
                }
                room.setAvailable(true);
                room.removeBookingTime(booking.getCheckinTime(), booking.getCheckoutTime());
                try {
                    roomRepository.save(room);
                } catch (Exception e) {
                    System.out.println("Error saving the room.");
                    e.printStackTrace();
                    return;
                }

                break;
            }
        }
    }

    public double calculateTotalCost(List<Booking> bookings) {
        double totalCost = 0.0;
        if (bookings.isEmpty()) {
            return totalCost;
        }

        for(Booking booking: bookings){
            Room room = booking.getRoom();
            LocalDateTime bookedStartTime = booking.getCheckinTime();
            LocalDateTime bookedEndTime = booking.getCheckoutTime();
            Duration duration = Duration.between(bookedStartTime.toLocalDate().atStartOfDay(), bookedEndTime.toLocalDate().atStartOfDay());
            long days = duration.toDays();
            double roomCost = room.getPrice() * (days > 0 ? days : 1);
            totalCost += roomCost;
        }
        return totalCost;
    }
    public void removeBooking(List<Booking> bookingsToRemove) {
        if (currentCustomer == null || bookingsToRemove.isEmpty()) {
            return;
        }
        try {
            for (Booking booking : bookingsToRemove) {
                bookingRepository.delete(booking);
            }
        }
        catch (Exception e) {
            System.out.println("Error deleting the booking.");
            e.printStackTrace();
        }

    }
    public boolean isRoomAvailable(Room room, LocalDateTime checkinTime, LocalDateTime checkoutTime) {
        List<Booking> bookings = bookingRepository.findByCustomerId(currentCustomer.getId());
        for (Booking booking : bookings) {
            if(booking.getRoom().getRoomNo() == room.getRoomNo()){
                if (!(checkoutTime.isBefore(booking.getCheckinTime()) || checkinTime.isAfter(booking.getCheckoutTime()))) {
                    return false;
                }
            }
        }
        return true;
    }

    private Room findRoom(int roomNumber) {
        List<Room> rooms = roomRepository.getAll();
        for (Room room : rooms) {
            if (room.getRoomNo() == roomNumber) {
                return room;
            }
        }
        return null;
    }

    public List<Booking> getBookingByCustomer() {
        if(currentCustomer == null){
            return new ArrayList<>();
        }
        return bookingRepository.findByCustomerId(currentCustomer.getId());
    }
}