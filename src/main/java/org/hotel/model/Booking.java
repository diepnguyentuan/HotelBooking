package org.hotel.model;

import java.time.LocalDateTime;

public class Booking {
    private int bookingId;
    private Customer customer;
    private Room room;
    private LocalDateTime checkinTime;
    private LocalDateTime checkoutTime;

    public Booking(int bookingId, Customer customer, Room room, LocalDateTime checkinTime, LocalDateTime checkoutTime) {
        this.bookingId = bookingId;
        this.customer = customer;
        this.room = room;
        this.checkinTime = checkinTime;
        this.checkoutTime = checkoutTime;
    }

    public Booking() {

    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public LocalDateTime getCheckinTime() {
        return checkinTime;
    }

    public void setCheckinTime(LocalDateTime checkinTime) {
        this.checkinTime = checkinTime;
    }

    public LocalDateTime getCheckoutTime() {
        return checkoutTime;
    }

    public void setCheckoutTime(LocalDateTime checkoutTime) {
        this.checkoutTime = checkoutTime;
    }
}