package org.example;


import org.example.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class ConsoleRunner implements CommandLineRunner {

    private final UserService userService;

    private final Scanner scanner = new Scanner(System.in);

    @Autowired
    public ConsoleRunner(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== Spring Boot CRUD Application ===");
        System.out.println("Available commands:");
        System.out.println("1 - Create user");
        System.out.println("2 - List all users");
        System.out.println("3 - Get user by ID");
        System.out.println("4 - Update user");
        System.out.println("5 - Delete user");
        System.out.println("0 - Exit");

        boolean running = true;

        while (running) {
            System.out.print("\nEnter command (1-5, 0 to exit): ");
            String command = scanner.nextLine();

            switch (command) {
                case "1":
                    createUser();
                    break;
                case "2":
                    listAllUsers();
                    break;
                case "3":
                    getUserById();
                    break;
                case "4":
                    updateUser();
                    break;
                case "5":
                    deleteUser();
                    break;
                case "0":
                    running = false;
                    System.out.println("Exiting application...");
                    break;
                default:
                    System.out.println("Invalid command. Please try again.");
            }
        }

        scanner.close();
    }

    private void createUser() {
        System.out.println("\n--- Create New User ---");
        System.out.print("Enter name: ");
        String name = scanner.nextLine();

        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        User user = new User(name, email);
        try {
            UserDto savedUser = userService.createUser(user);
            System.out.println("User created successfully: " + savedUser);
        } catch (Exception e) {
            System.out.println("Error creating user: " + e.getMessage());
        }
    }

    private void listAllUsers() {
        System.out.println("\n--- All Users ---");
        userService.getAllUsers().forEach(user ->
                System.out.println("ID: " + user.getId() +
                        ", Name: " + user.getName() +
                        ", Email: " + user.getEmail())
        );

        if (userService.getAllUsers().isEmpty()) {
            System.out.println("No users found.");
        }
    }

    private void getUserById() {
        System.out.println("\n--- Get User by ID ---");
        System.out.print("Enter user ID: ");

        try {
            Long id = Long.parseLong(scanner.nextLine());
            System.out.println(userService.getUserById(id));
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please enter a number.");
        }
    }

    private void updateUser() {
        System.out.println("\n--- Update User ---");
        System.out.print("Enter user ID to update: ");

        try {

            Long id = Long.parseLong(scanner.nextLine());

            userService.getUserById(id).ifPresentOrElse(
                    existingUser -> {
                        System.out.print("Enter new name (current: " + existingUser.getName() + "): ");
                        String name = scanner.nextLine();

                        System.out.print("Enter new email (current: " + existingUser.getEmail() + "): ");
                        String email = scanner.nextLine();

                        UserDto userDto = new UserDto(
                                name.isEmpty() ? existingUser.getName() : name,
                                email.isEmpty() ? existingUser.getEmail() : email
                        );

                        userService.updateUser(id, userDto).ifPresentOrElse(
                                user -> System.out.println("User updated successfully: " + user),
                                () -> System.out.println("Failed to update user.")
                        );
                    },
                    () -> System.out.println("User not found with ID: " + id)
            );
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please enter a number.");
        }
    }

    private void deleteUser() {
        System.out.println("\n--- Delete User ---");
        System.out.print("Enter user ID to delete: ");

        try {
            Long id = Long.parseLong(scanner.nextLine());

            if (userService.deleteUser(id)) {
                System.out.println("User deleted successfully.");
            } else {
                System.out.println("User not found with ID: " + id);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please enter a number.");
        }
    }
}