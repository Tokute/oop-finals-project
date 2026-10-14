import java.io.IOException;
import java.util.Scanner;
import users.Admin;
import users.TeachingAssistant;
import users.User;
import tasks.GradingTask;
import tasks.Task;
import tasks.TeachingTask;
import utils.IDCreator;
import utils.TimeUtils;

public class Main {
    public static void main(String[] args) throws IOException {
        var scanner = new Scanner(System.in);
        ScheduleManager manager = new ScheduleManager();

        // Check if we should generate test data (only if database is empty or doesn't exist)
        java.io.File dbFile = new java.io.File(ScheduleManager.DATABASE_FILE);
        boolean generateTestData = !dbFile.exists() || dbFile.length() == 0;

        Admin admin = null;
        for (User existingUser : manager.getRegisteredUsers()) {
            if (existingUser instanceof Admin) {
                admin = (Admin) existingUser;
                break;
            }
        }

        if (admin == null) {
            admin = new Admin("Admin User", "pass123", IDCreator.createAdminID());
            manager.registerUser(admin, admin);
            manager.saveToDatabase();
        }
        RegistrationService registrationService = new RegistrationService(scanner, manager, admin);

        // Only generate test data if database is empty (first run)
        if (generateTestData) {
            // Generate and register 3 testing TeachingAssistant users with unique properties
            String[][] taExpertise = {
                {"Java", "Math", "Algorithms"},
                {"Python", "Data Science", "Machine Learning"},
                {"C++", "Algorithms", "Operating Systems"}
            };
            String[][] taPreferences = {
                {"Morning", "Flexible"},
                {"Afternoon", "Evening"},
                {"Night", "Weekends"}
            };
            String[][] taTimeDetails = {
                {"Tue 00:00-23:59", "Wed 00:00-23:59", "Fri 00:00-23:59"},
                {"Mon 08:00-16:00", "Wed 08:00-16:00", "Fri 08:00-16:00"},
                {"Tue 18:00-22:00", "Thu 18:00-22:00", "Sat 09:00-17:00"}
            };

            for (int i = 0; i < 3; i++) {
                users.TeachingAssistant ta = RegistrationService.createTeachingAssistant(
                    "TA_" + (i + 1),
                    "pass" + (i + 1) + "3",
                    taExpertise[i],
                    taPreferences[i],
                    taTimeDetails[i]
                );
                manager.registerTeachingAssistant(ta, admin);
            }

            // Generate and register 3 testing TeachingTask objects with unique properties
            String[][] ttExpertise = {
                {"Java", "OOP"},
                {"Python", "Pandas", "NumPy"},
                {"C++", "STL", "Memory Management"}
            };
            String[][] ttPreferences = {
                {"Morning"},
                {"Afternoon"},
                {"Evening"}
            };
            String[][] ttTimeDetails = {
                {"Tue 09:00-15:00", "Thu 09:00-15:00"},
                {"Mon 10:00-12:00", "Wed 10:00-12:00", "Fri 10:00-12:00"},
                {"Tue 18:00-20:00", "Thu 18:00-20:00"}
            };
            double[] ttWorkHours = {6.0, 4.0, 5.0};
            String[] ttCourseTypes = {"Programming", "Data Analysis", "Systems"};

            for (int i = 0; i < 3; i++) {
                Task teachingTask = RegistrationService.createTeachingTask(
                    "Task_" + (i + 1),
                    ttWorkHours[i],
                    ttExpertise[i],
                    ttPreferences[i],
                    ttTimeDetails[i],
                    ttCourseTypes[i]
                );
                manager.registerTask(teachingTask, admin);
            }

            // Generate and register 3 testing GradingTask objects with unique properties
            String[][] gtExpertise = {
                {"Writing", "Assessment"},
                {"Math", "Problem Solving"},
                {"Programming", "Debugging"}
            };
            String[][] gtPreferences = {
                {"Night"},
                {"Morning"},
                {"Flexible"}
            };
            String[][] gtTimeDetails = {
                {"Fri 13:00-18:00"},
                {"Sat 09:00-12:00", "Sun 14:00-17:00"},
                {"Mon 19:00-21:00", "Wed 19:00-21:00"}
            };
            double[] gtWorkHours = {5.0, 3.0, 4.0};
            int[] gtTotalPapers = {40, 25, 30};

            for (int i = 0; i < 3; i++) {
                Task gradingTask = RegistrationService.createGradingTask(
                    "GradingTask_" + (i + 1),
                    gtWorkHours[i],
                    gtExpertise[i],
                    gtPreferences[i],
                    gtTimeDetails[i],
                    gtTotalPapers[i]
                );
                manager.registerTask(gradingTask, admin);
            }

            manager.saveToDatabase();
        }

        while (true) {
            printMenu();
            System.out.print("Choose an option: ");

            String input = scanner.nextLine();
            int choice;

            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number from the menu.");
                continue;
            }

            switch (choice) {
                case 1:
                    manager.viewUsers();
                    break;

                case 2:
                    manager.viewTasks();
                    break;

                case 3:
                    try {
                        manager.calculateAvailableShift();
                    } catch (Exception e) {
                        System.err.println("An error occurred while checking compatible shifts: " + e.getMessage());
                        e.printStackTrace();
                    }
                    System.out.println("===============================");
                    System.out.println("\nCompatible shifts:");
                    if (manager.getCompatibleShifts().isEmpty()) {
                        System.out.println("No compatible assignment found.");
                    } else {
                        for (String shift : manager.getCompatibleShifts()) {
                            System.out.println(shift);
                        }
                    }
                    System.out.println("\n===============================");
                    break;

                case 4:
                    try {
                        registrationService.registerNewUserOrTask();
                    } catch (Exception e) {
                        System.err.println("An error occurred during registration: " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;

                case 5:
                    modifyRecord(scanner, manager, admin);
                    break;

                case 6:
                    deleteRecord(scanner, manager, admin);
                    break;

                case 7:
                    System.out.println("Exiting program...");
                    scanner.close();
                    return;

                default:
                    System.out.println("Unknown option. Please try again.");
                    break;
            }
        }
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("========== Main Menu ==========");
        System.out.println("1. View all users");
        System.out.println("2. View all tasks");
        System.out.println("3. Check compatible shifts");
        System.out.println("4. Register new User or Task");
        System.out.println("5. Modify a User or Task");
        System.out.println("6. Delete a User or Task");
        System.out.println("7. Exit");
    }

    private static void modifyRecord(Scanner scanner, ScheduleManager manager, Admin admin) {
        System.out.println("Modify which record?");
        System.out.println("1. User");
        System.out.println("2. Task");
        System.out.print("Enter your choice: ");

        String type = scanner.nextLine().trim();
        try {
            if ("1".equals(type)) {
                modifyUser(scanner, manager, admin);
            } else if ("2".equals(type)) {
                modifyTask(scanner, manager, admin);
            } else {
                System.out.println("Invalid choice.");
            }
        } catch (IOException e) {
            System.out.println("Could not save the modification: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid modification: " + e.getMessage());
        }
    }

    private static void modifyUser(Scanner scanner, ScheduleManager manager, Admin admin) throws IOException {
        manager.viewUsers();
        System.out.print("Enter the ID of the user to modify: ");
        String id = scanner.nextLine().trim();
        User existing = findUser(manager, id);
        if (existing == null) {
            System.out.println("No user found with ID " + id + ".");
            return;
        }

        System.out.print("Enter name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        User updated;
        if (existing instanceof TeachingAssistant) {
            System.out.print("Enter expertise (comma-separated): ");
            String[] expertise = parseCommaSeparatedString(scanner.nextLine());
            System.out.print("Enter preferences (comma-separated): ");
            String[] preferences = parseCommaSeparatedString(scanner.nextLine());
            String[] availableTime = TimeUtils.readValidatedTimeSlots(
                    scanner, "Enter available times (comma-separated, format: Day HH:MM-HH:MM): ");
            updated = new TeachingAssistant(name, password, existing.getId(),
                    expertise, preferences, availableTime);
        } else if (existing instanceof Admin) {
            updated = new Admin(name, password, existing.getId());
        } else {
            System.out.println("This user type cannot be modified here.");
            return;
        }

        if (manager.updateUser(updated, admin)) {
            System.out.println("User updated successfully.");
            updated.printDetails();
        } else {
            System.out.println("User could not be updated.");
        }
    }

    private static void modifyTask(Scanner scanner, ScheduleManager manager, Admin admin) throws IOException {
        manager.viewTasks();
        System.out.print("Enter the ID of the task to modify: ");
        String id = scanner.nextLine().trim();
        Task existing = findTask(manager, id);
        if (existing == null) {
            System.out.println("No task found with ID " + id + ".");
            return;
        }

        System.out.print("Enter name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter work hours: ");
        double workHours = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("Enter expertise (comma-separated): ");
        String[] expertise = parseCommaSeparatedString(scanner.nextLine());
        System.out.print("Enter preferences (comma-separated): ");
        String[] preferences = parseCommaSeparatedString(scanner.nextLine());
        String[] timeOccupied = TimeUtils.readValidatedTimeSlots(
                scanner, "Enter occupied times (comma-separated, format: Day HH:MM-HH:MM): ");

        Task updated;
        if (existing instanceof TeachingTask) {
            System.out.print("Enter course type: ");
            String courseType = scanner.nextLine().trim();
            updated = new TeachingTask(name, existing.getId(), workHours,
                    expertise, preferences, timeOccupied, courseType);
        } else if (existing instanceof GradingTask) {
            System.out.print("Enter total papers: ");
            int totalPapers = Integer.parseInt(scanner.nextLine().trim());
            updated = new GradingTask(name, existing.getId(), workHours,
                    expertise, preferences, timeOccupied, totalPapers);
        } else {
            System.out.println("This task type cannot be modified here.");
            return;
        }

        if (manager.updateTask(updated, admin)) {
            System.out.println("Task updated successfully.");
            updated.printDetails();
        } else {
            System.out.println("Task could not be updated.");
        }
    }

    private static void deleteRecord(Scanner scanner, ScheduleManager manager, Admin admin) {
        System.out.println("Delete which record?");
        System.out.println("1. User");
        System.out.println("2. Task");
        System.out.print("Enter your choice: ");

        String type = scanner.nextLine().trim();
        if (!"1".equals(type) && !"2".equals(type)) {
            System.out.println("Invalid choice.");
            return;
        }

        if ("1".equals(type)) {
            manager.viewUsers();
        } else {
            manager.viewTasks();
        }
        System.out.print("Enter the ID of the record to delete: ");
        String id = scanner.nextLine().trim();
        System.out.print("Delete record " + id + "? (y/n): ");
        String confirmation = scanner.nextLine().trim();
        if (!confirmation.equalsIgnoreCase("y") && !confirmation.equalsIgnoreCase("yes")) {
            System.out.println("Deletion cancelled.");
            return;
        }

        try {
            boolean deleted = "1".equals(type)
                    ? manager.deleteUser(id, admin)
                    : manager.deleteTask(id, admin);
            System.out.println(deleted ? "Record deleted successfully." : "No matching record found.");
        } catch (IOException e) {
            System.out.println("Could not save the deletion: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println("Deletion not allowed: " + e.getMessage());
        }
    }

    private static User findUser(ScheduleManager manager, String id) {
        for (User user : manager.getRegisteredUsers()) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }

    private static Task findTask(ScheduleManager manager, String id) {
        for (Task task : manager.getRegisteredTasks()) {
            if (task.getId().equals(id)) {
                return task;
            }
        }
        return null;
    }

    private static String[] parseCommaSeparatedString(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new String[0];
        }
        return input.split("\\s*,\\s*");
    }

}