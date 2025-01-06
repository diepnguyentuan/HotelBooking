package org.hotel.service;

import org.hotel.model.Booking;
import org.hotel.model.Customer;
import org.hotel.model.Room;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class HotelRespond {
    private HotelService hotelService;

    public HotelRespond(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    public void showCustomersAndBookedRooms() {
        List<Customer> customers = hotelService.getCustomers();
        if (customers.isEmpty()) {
            System.out.println("No customers have booked rooms yet.");
            return;
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String header = String.format("| %-15s | %-15s | %-17s | %-10s | %-20s | %-10s |%n",
                "Customer Name", "Room Number", "Type Room", "Price", "Booking Time", "Booking ID");
        printTableSeparator(header.length());
        System.out.print(header);
        printTableSeparator(header.length());

        for (Customer customer : customers) {
            List<Booking> bookings = hotelService.getBookingByCustomer(customer);
            if (bookings.isEmpty()) {
                System.out.format("| %-15s | %-15s | %-17s | %-10s | %-20s | %-10s |%n",
                        customer.getName(), "None", "None", "None", "None", "None");
                printTableSeparator(header.length());

            } else {
                for (Booking booking : bookings) {
                    Room room = booking.getRoom();
                    String bookingTime = dtf.format(booking.getCheckinTime()) + " to " + dtf.format(booking.getCheckoutTime());
                    System.out.format("| %-15s | %-15d | %-15s | %-10.2f | %-20s | %-10d |%n",
                            customer.getName(), room.getRoomNo(), room.getTypeRoom(), room.getPrice(), bookingTime, booking.getBookingId());
                    printTableSeparator(header.length());
                }
            }
        }
    }

    public void showAvailableRooms() {
        List<Room> rooms = hotelService.getRooms();
        if (rooms.isEmpty()) {
            System.out.println("No rooms available");
            return;
        }

        boolean found = false;
        String header = String.format("| %-15s | %-15s | %-17s | %-10s |%n",
                "Room Number", "Type Room", "Available", "Price");
        printTableSeparator(header.length());
        System.out.print(header);
        printTableSeparator(header.length());
        for (Room room : rooms) {
            if (!room.isAvailable()) {
                continue;
            }
            System.out.format("| %-15d | %-15s | %-17s | %-10.2f $/day|%n",
                    room.getRoomNo(), room.getTypeRoom(),
                    room.isAvailable() ? "Yes" : "No", room.getPrice());
            printTableSeparator(header.length());
            found = true;
        }
        if (!found) {
            System.out.println("No rooms available");
        }
    }

    public void bookRoom(Scanner scanner) {
        System.out.println("Enter the room number you want to book:");
        int roomNumber = getValidInt(scanner, "Please enter a valid room number", 101, 200);

        LocalDate checkinDate = getValidDate(scanner, "Please enter check-in date (dd/MM/yyyy): ");
        if (checkinDate == null) return;

        LocalDate checkoutDate = getValidDate(scanner, "Please enter check-out date (dd/MM/yyyy): ");
        if (checkoutDate == null) return;

        LocalDateTime checkinDateTime = checkinDate.atStartOfDay();
        LocalDateTime checkoutDateTime = checkoutDate.atStartOfDay();
        Room room = hotelService.findRoom(roomNumber);
        if(room == null){
            System.out.println("Room with number "+ roomNumber + " does not exist.");
            return;
        }
        if (!hotelService.isRoomAvailable(room, checkinDateTime, checkoutDateTime)) {
            System.out.println("Room " + roomNumber + " is already booked in this time range. Please choose a different time.");
            return;
        }
        hotelService.bookRoom(roomNumber, checkinDateTime, checkoutDateTime);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        System.out.println("Room " + roomNumber + " booked from " + checkinDate.format(dtf) + " to " + checkoutDate.format(dtf));
    }

    public void cancelRoom(Scanner scanner) {
        System.out.println("Enter the room number you want to cancel:");
        int roomNumber = getValidInt(scanner, "Please enter a valid number", 101, 200);
        hotelService.cancelRoom(roomNumber);
        System.out.println("Room " + roomNumber + " has been cancelled successfully");
    }

    public void viewBookedRooms() {
        List<Booking> bookings = hotelService.getBookingByCustomer();
        if (bookings.isEmpty()) {
            System.out.println("No rooms booked by current customer.");
            return;
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String header = String.format("| %-15s | %-15s | %-15s | %-10s | %-20s | %-10s |%n",
                "Room Number", "Type Room", "Available", "Price", "Booking Time", "Booking ID");
        System.out.println("Your booked rooms are: ");
        printTableSeparator(header.length());
        System.out.print(header);
        printTableSeparator(header.length());
        for (Booking booking : bookings) {
            Room room = booking.getRoom();
            String bookingTime = dtf.format(booking.getCheckinTime()) + " to " + dtf.format(booking.getCheckoutTime());
            System.out.format("| %-15d | %-15s | %-15s | %-10.2f $/day| %-20s | %-10d |%n",
                    room.getRoomNo(), room.getTypeRoom(),
                    room.isAvailable() ? "Yes" : "No", room.getPrice(), bookingTime, booking.getBookingId());
            printTableSeparator(header.length());
        }
    }

    public void checkoutRoom(String outputFilePath, Scanner scanner) {
        Customer currentCustomer = hotelService.getCurrentCustomer();
        if (currentCustomer == null) {
            System.out.println("Please log in first.");
            return;
        }

        List<Booking> customerBookings = hotelService.getBookingByCustomer();
        if (customerBookings.isEmpty()) {
            System.out.println("No booked rooms to checkout.");
            return;
        }

        // Display booked rooms before checkout prompt
        System.out.println("Please check your booking information:");
        viewBookedRooms();


        List<Booking> bookingsToCheckout = new ArrayList<>();
        boolean checkoutAll = false; // Thêm biến flag để kiểm tra xem người dùng có muốn checkout tất cả hay không

        while (true) {
            System.out.println("Enter the booking ID you want to checkout (or 0 to checkout all rooms): ");
            int bookingId = getValidInt(scanner, "Please enter a valid booking ID:", 0, Integer.MAX_VALUE);

            if (bookingId == 0) {
                bookingsToCheckout.addAll(customerBookings);
                checkoutAll = true; // Gán giá trị true để biết người dùng muốn checkout tất cả
                break;
            }

            Booking booking = findBooking(bookingId, customerBookings);
            if (booking == null) {
                System.out.println("Booking with ID " + bookingId + " does not exist. Please try again.");
                continue;
            }

            if (!bookingsToCheckout.contains(booking)) {
                bookingsToCheckout.add(booking);
            }

            System.out.println("Do you want to checkout another room? (y/n): ");
            String choice = scanner.next();
            if (!choice.equalsIgnoreCase("y")) {
                break;
            }

        }

        if (bookingsToCheckout.isEmpty()){
            System.out.println("No bookings to checkout");
            return;
        }

        try (BufferedWriter br = new BufferedWriter(new FileWriter(outputFilePath, true))) {
            br.write("--------------- Checkout Information ---------------");
            br.newLine();
            br.write("Customer Name: " + currentCustomer.getName());
            br.newLine();

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            double totalCost = 0;
            totalCost = hotelService.calculateTotalCost(bookingsToCheckout);

            for (Booking booking : bookingsToCheckout) {
                Room room = booking.getRoom();

                LocalDateTime bookedStartTime = booking.getCheckinTime();
                LocalDateTime checkoutTime = LocalDateTime.now();

                String roomInfo = String.format("Room %d (%s) - %.2f/day: %.2f$", room.getRoomNo(), room.getTypeRoom(), room.getPrice(), (checkoutTime.toLocalDate().toEpochDay() - bookedStartTime.toLocalDate().toEpochDay()) * room.getPrice());

                System.out.println(roomInfo);
                br.write(roomInfo);
                br.newLine();

                br.write("Check-in date: " + dtf.format(bookedStartTime.toLocalDate()));
                br.newLine();
                br.write("Check-out date: " + dtf.format(checkoutTime.toLocalDate()));
                br.newLine();
            }

            hotelService.removeBooking(bookingsToCheckout); // Xóa các booking đã checkout
            if (checkoutAll) {
                System.out.println("All bookings have been checked out successfully.");
            } else {
                System.out.println("Booking(s) checked out successfully.");
            }
            String totalCostInfo = "Total cost: " + totalCost + " $";
            System.out.println(totalCostInfo);
            br.write(totalCostInfo);
            br.newLine();
            br.write("---------------------------------------------------");
            br.newLine();

        } catch (IOException e) {
            System.out.println("Error writing to file.");
            e.printStackTrace();
        }
    }


    public void loginCustomer(Scanner scanner) {
        System.out.println("Please enter your name: ");
        String name = scanner.nextLine();
        hotelService.loginCustomer(name);
        System.out.println("Customer: " + name);
    }

    public void logout() {
        hotelService.logout();
        System.out.println("Customer has been logged out");
    }

    private int getValidInt(Scanner scanner, String message, int min, int max) {
        int choice;
        while (true) {
            System.out.println(message);
            try {
                choice = scanner.nextInt();
                scanner.nextLine();
                if (choice < min || choice > max) {
                    System.out.println("Invalid choice");
                    continue;
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input, please enter a number");
                scanner.nextLine();
            }
        }
        return choice;
    }

    private LocalDate getValidDate(Scanner scanner, String message) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate date = null;
        while (true) {
            System.out.println(message);
            String dateString = scanner.nextLine();
            try {
                date = LocalDate.parse(dateString, dtf);
                LocalDate today = LocalDate.now();
                if (date.isBefore(today)) {
                    System.out.println("Date must not before today, try again");
                    continue;
                }
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format, try again");
            }
        }
        return date;
    }
    private Room findRoom(int roomNumber) {
        for (Room room : hotelService.getRooms()) {
            if (room.getRoomNo() == roomNumber) {
                return room;
            }
        }
        return null;
    }

    private Booking findBooking(int bookingId, List<Booking> bookings) {
        for (Booking booking : bookings) {
            if (booking.getBookingId() == bookingId) {
                return booking;
            }
        }
        return null;
    }

    private void printTableSeparator(int length) {
        System.out.print("+");
        for (int i = 0; i < length - 2; i++) {
            System.out.print("-");
        }
        System.out.println("+");
    }
}