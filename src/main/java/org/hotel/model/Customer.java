package org.hotel.model;

import java.util.ArrayList;
import java.util.List;

public class Customer {
    private int id;
    private String name;

    private List<Room> bookedRooms;

    public Customer(String name) {
        this.name = name;
        this.bookedRooms = new ArrayList<>();
    }
    public Customer() {

    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Room> getBookedRooms() {
        return bookedRooms;
    }

    public void setBookedRooms(List<Room> bookedRooms) {
        this.bookedRooms = bookedRooms;
    }
    public void addBookedRoom(Room room) {
        bookedRooms.add(room);
    }

    public void removeBookedRoom(Room room) {
        bookedRooms.remove(room);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}