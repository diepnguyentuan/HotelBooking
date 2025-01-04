package org.hotel.HotelManager;
import org.hotel.dao.BookingDAO;
import org.hotel.dao.CustomerDAO;
import org.hotel.dao.RoomDAO;
import org.hotel.model.Customer;
import org.hotel.model.Room;
import org.hotel.repository.BookingRepository;
import org.hotel.repository.CustomerRepository;
import org.hotel.repository.RoomRepository;
import org.hotel.service.HotelRespond;
import org.hotel.service.HotelService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HotelManager {
    private HotelService hotelService;
    private HotelRespond hotelRespond;

    public HotelManager() {
        CustomerRepository customerRepository = new CustomerDAO();
        RoomRepository roomRepository = new RoomDAO();
        BookingRepository bookingRepository = new BookingDAO();
        hotelService = new HotelService(customerRepository, roomRepository, bookingRepository);
        hotelRespond = new HotelRespond(hotelService);


    }
    public void run() {
        Scanner scanner = new Scanner(System.in);
        String outputFilePath = "checkout_history.txt";

        while(true) {
            System.out.println("----------------------");
            System.out.println("1. Login");
            System.out.println("2. Show All Customer and Booked Room");
            System.out.println("3. Show Available Room");
            System.out.println("4. Book Room");
            System.out.println("5. Cancel Room");
            System.out.println("6. View Booked Room");
            System.out.println("7. Checkout Room");
            System.out.println("8. Logout");
            System.out.println("9. Exit");
            System.out.println("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    hotelRespond.loginCustomer(scanner);
                    break;
                case 2:
                    hotelRespond.showCustomersAndBookedRooms();
                    break;
                case 3:
                    hotelRespond.showAvailableRooms();
                    break;
                case 4:
                    hotelRespond.bookRoom(scanner);
                    break;
                case 5:
                    hotelRespond.cancelRoom(scanner);
                    break;
                case 6:
                    hotelRespond.viewBookedRooms();
                    break;
                case 7:
                    hotelRespond.checkoutRoom(outputFilePath, scanner);
                    break;
                case 8:
                    hotelRespond.logout();
                    break;
                case 9:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice");
            }
        }
    }

    public static void main(String[] args) {
        HotelManager hotelManager = new HotelManager();
        hotelManager.run();
    }
}