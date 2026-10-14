import java.io.IOException;
import java.util.Scanner;
import users.Admin;
import users.User;
import tasks.Task;
import utils.IDCreator;

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
        System.out.println("5. Exit");
    }

}