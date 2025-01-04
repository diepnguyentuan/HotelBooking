package org.hotel.repository;

import org.hotel.model.Customer;

import java.util.List;

public interface CustomerRepository {
    void save(Customer customer);
    Customer findByName(String name);
    List<Customer> getAll();
}
