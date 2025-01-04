package org.hotel.dao;
import org.hotel.config.DatabaseConfig;
import org.hotel.model.Customer;
import org.hotel.repository.CustomerRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO implements CustomerRepository {
    @Override
    public void save(Customer customer) {
        String sql = "INSERT INTO customers (name) VALUES (?)";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, customer.getName());
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        customer.setId(generatedKeys.getInt(1)); // ID tu sinh
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public Customer findByName(String name) {
        String sql = "SELECT * FROM customers WHERE name = ?";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Customer customer = new Customer(resultSet.getString("name"));
                    customer.setId(resultSet.getInt("id"));
                    return customer;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public List<Customer> getAll() {
        String sql = "SELECT * FROM customers";
        List<Customer> customers = new ArrayList<>();
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Customer customer = new Customer(resultSet.getString("name"));
                customer.setId(resultSet.getInt("id"));
                customers.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }
    public Customer getById(int customerId) {
        String sql = "SELECT * FROM customers WHERE id = ?";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, customerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Customer customer = new Customer(resultSet.getString("name"));
                    customer.setId(resultSet.getInt("id"));
                    return customer;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}