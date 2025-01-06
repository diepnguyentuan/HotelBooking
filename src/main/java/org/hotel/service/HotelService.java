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
    public void bookRoom(int roomNumber, LocalDateTime checkinTime, LocalDateTime checkoutTime) {
        Room room = findRoom(roomNumber);
        Customer currentCustomer = getCurrentCustomer();
        if(room != null && currentCustomer != null){
            Booking booking = new Booking(currentCustomer, room, checkinTime, checkoutTime);
            bookingRepository.save(booking); // Gọi bookingRepository.save thay vì roomRepository.save
            // Find the booking id, since the id is generated in database, we should find by other parameters such as time and customer id
            Booking savedBooking = findBookingBy(currentCustomer.getId(),room.getRoomNo(), checkinTime, checkoutTime);
            booking.setBookingId(savedBooking.getBookingId());
            System.out.println("Booking created successfully!");
        }
    }
    //Helper function to find booking by customer and time
    public Booking findBookingBy(int customerId, int roomId, LocalDateTime checkinTime, LocalDateTime checkoutTime){
        return bookingRepository.findBookingBy(customerId, roomId, checkinTime, checkoutTime);
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
                    System.out.println("Room " + roomNumber + " has been cancelled successfully");
                } catch (Exception e) {
                    System.out.println("Error deleting the booking.");
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
        if (currentCustomer == null) {
            System.out.println("You must login to do this function.");
            return;
        }
        if (bookingsToRemove.isEmpty()) {
            System.out.println("No bookings to remove.");
            return;
        }

        try {
            if (bookingsToRemove.get(0).getBookingId() == 0){
                bookingRepository.deleteAll();
                System.out.println("All bookings have been checked out.");
            }
            else {
                for (Booking booking : bookingsToRemove) {
                    bookingRepository.delete(booking);
                }
            }
        } catch (Exception e) {
            System.out.println("Error deleting booking(s).");
            e.printStackTrace();
        }
    }
    public boolean isRoomAvailable(Room room, LocalDateTime checkinTime, LocalDateTime checkoutTime) {
        List<Booking> bookings = bookingRepository.findByRoomId(room.getRoomNo());
        for (Booking booking : bookings) {
            if (!(checkoutTime.isBefore(booking.getCheckinTime()) || checkinTime.isAfter(booking.getCheckoutTime()))) {
                return false;
            }
        }
        return true;
    }

    public Room findRoom(int roomNumber) {
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
    public List<Booking> getBookingByCustomer(Customer customer) {
        if(customer == null){
            return new ArrayList<>();
        }
        return bookingRepository.findByCustomerId(customer.getId());
    }

}