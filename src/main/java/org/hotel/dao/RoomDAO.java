package org.hotel.dao;

import org.hotel.config.DatabaseConfig;
import org.hotel.model.Room;
import org.hotel.repository.RoomRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class RoomDAO implements RoomRepository {
    @Override
    public void save(Room room) {
        String sql = "INSERT INTO rooms (room_no, type_room, price, available) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, room.getRoomNo());
            statement.setString(2, room.getTypeRoom());
            statement.setDouble(3, room.getPrice());
            statement.setBoolean(4, room.isAvailable());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public List<Room> getAll() {
        String sql = "SELECT * FROM rooms";
        List<Room> rooms = new ArrayList<>();
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Room room = new Room(resultSet.getInt("room_no"),
                        resultSet.getString("type_room"),
                        resultSet.getDouble("price"),
                        resultSet.getBoolean("available"));
                rooms.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }
    public Room getById(int roomId) {
        String sql = "SELECT * FROM rooms WHERE room_no = ?";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, roomId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Room(resultSet.getInt("room_no"),
                            resultSet.getString("type_room"),
                            resultSet.getDouble("price"),
                            resultSet.getBoolean("available"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}