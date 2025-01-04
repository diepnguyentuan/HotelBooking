package org.hotel.repository;

import org.hotel.model.Room;

import java.util.List;

public interface RoomRepository {
    void save(Room room);
    List<Room> getAll();
}
