package org.hotel.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Room {
    private int roomNo;
    private String typeRoom;
    private double price;
    private boolean available;
    private List<LocalDateTime[]> bookedTimes = new ArrayList<>();

    public Room(int roomNo, String typeRoom, double price, boolean available) {
        this.roomNo = roomNo;
        this.typeRoom = typeRoom;
        this.price = price;
        this.available = available;
    }

    public Room() {
    }

    public int getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(int roomNo) {
        this.roomNo = roomNo;
    }

    public String getTypeRoom() {
        return typeRoom;
    }

    public void setTypeRoom(String typeRoom) {
        this.typeRoom = typeRoom;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public List<LocalDateTime[]> getBookedTimes() {
        return bookedTimes;
    }

    public void setBookedTimes(List<LocalDateTime[]> bookedTimes) {
        this.bookedTimes = bookedTimes;
    }

    public void addBookingTime(LocalDateTime start, LocalDateTime end) {
        LocalDateTime[] bookingTime = {start, end};
        bookedTimes.add(bookingTime);
    }

    public void removeBookingTime(LocalDateTime startTime, LocalDateTime endTime) {
        bookedTimes.removeIf(times -> times[0].equals(startTime) && times[1].equals(endTime));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return roomNo == room.roomNo;
    }
}