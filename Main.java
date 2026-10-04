import java.util.Scanner;
import users.Admin;
import users.TeachingAssistant;
import tasks.Task;
import tasks.TeachingTask;
import tasks.GradingTask;

public class Main {
    public static void main(String[] args) {
        var scanner = new Scanner(System.in);
        ScheduleManager manager = new ScheduleManager();
        Admin admin = new Admin("Admin User", "pass123", IDCreator.createAdminID());
        manager.registerUser(admin, admin);

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
            TeachingAssistant ta = createTeachingAssistant(
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
            Task teachingTask = createTeachingTask(
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
            Task gradingTask = createGradingTask(
                "GradingTask_" + (i + 1),
                gtWorkHours[i],
                gtExpertise[i],
                gtPreferences[i],
                gtTimeDetails[i],
                gtTotalPapers[i]
            );
            manager.registerTask(gradingTask, admin);
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
                    manager.calculateAvailableShift();
                    System.out.println("==============================");
                    System.out.println("\nCompatible shifts:");
                    if (manager.getCompatibleShifts().isEmpty()) {
                        System.out.println("No compatible assignment found.");
                    } else {
                        for (String shift : manager.getCompatibleShifts()) {
                            System.out.println(shift);
                        }
                    }
                    System.out.println("\n==============================");
                    break;

                case 4:
                    registerNewUserOrTask(scanner, manager, admin);
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

    private static TeachingAssistant createTeachingAssistant(String name, String password, String[] expertise, String[] preferences, String[] timeDetails) {
        return new TeachingAssistant(
                name,
                password,
                IDCreator.createTeachingAssistantID(),
                expertise,
                preferences,
                timeDetails
        );
    }

    private static Task createTeachingTask(String name, double workHours, String[] expertise, String[] preference, String[] timeOccupied, String courseType) {
        return new TeachingTask(
                name,
                IDCreator.createTeachingTaskID(),
                workHours,
                expertise,
                preference,
                timeOccupied,
                courseType
        );
    }

    private static Task createGradingTask(String name, double workHours, String[] expertise, String[] preference, String[] timeOccupied, int totalPapers) {
        return new GradingTask(
                name,
                IDCreator.createGradingTaskID(),
                workHours,
                expertise,
                preference,
                timeOccupied,
                totalPapers
        );
    }

    private static void registerNewUserOrTask(Scanner scanner, ScheduleManager manager, Admin admin) {
        System.out.println();
        System.out.println("What would you like to register?");
        System.out.println("1. TeachingAssistant");
        System.out.println("2. TeachingTask");
        System.out.println("3. GradingTask");
        System.out.print("Enter your choice: ");

        String input = scanner.nextLine();
        int subChoice;

        try {
            subChoice = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        switch (subChoice) {
            case 1:
                registerTeachingAssistant(scanner, manager, admin);
                break;
            case 2:
                registerTeachingTask(scanner, manager, admin);
                break;
            case 3:
                registerGradingTask(scanner, manager, admin);
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
                break;
        }
    }

    private static void registerTeachingAssistant(Scanner scanner, ScheduleManager manager, Admin admin) {
        System.out.println();
        System.out.println("=== Register TeachingAssistant ===");
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        System.out.print("Enter expertise (comma-separated): ");
        String[] expertise = parseCommaSeparatedString(scanner.nextLine());
        System.out.print("Enter preferences (comma-separated): ");
        String[] preferences = parseCommaSeparatedString(scanner.nextLine());
        System.out.print("Enter time details (comma-separated, format: Day HH:MM-HH:MM): ");
        String[] timeDetails = parseCommaSeparatedString(scanner.nextLine());

        TeachingAssistant ta = createTeachingAssistant(name, password, expertise, preferences, timeDetails);
        manager.registerTeachingAssistant(ta, admin);
        System.out.println("TeachingAssistant registered successfully!");
        ta.printDetails();
    }

    private static void registerTeachingTask(Scanner scanner, ScheduleManager manager, Admin admin) {
        System.out.println();
        System.out.println("=== Register TeachingTask ===");
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter work hours: ");
        double workHours = Double.parseDouble(scanner.nextLine());

        System.out.print("Enter expertise (comma-separated): ");
        String[] expertise = parseCommaSeparatedString(scanner.nextLine());
        System.out.print("Enter preference (comma-separated): ");
        String[] preference = parseCommaSeparatedString(scanner.nextLine());
        System.out.print("Enter time occupied (comma-separated, format: Day HH:MM-HH:MM): ");
        String[] timeOccupied = parseCommaSeparatedString(scanner.nextLine());
        System.out.print("Enter course type: ");
        String courseType = scanner.nextLine();

        Task task = createTeachingTask(name, workHours, expertise, preference, timeOccupied, courseType);
        manager.registerTask(task, admin);
        System.out.println("TeachingTask registered successfully!");
        task.printDetails();
    }

    private static void registerGradingTask(Scanner scanner, ScheduleManager manager, Admin admin) {
        System.out.println();
        System.out.println("=== Register GradingTask ===");
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter work hours: ");
        double workHours = Double.parseDouble(scanner.nextLine());

        System.out.print("Enter expertise (comma-separated): ");
        String[] expertise = parseCommaSeparatedString(scanner.nextLine());
        System.out.print("Enter preference (comma-separated): ");
        String[] preference = parseCommaSeparatedString(scanner.nextLine());
        System.out.print("Enter time occupied (comma-separated, format: Day HH:MM-HH:MM): ");
        String[] timeOccupied = parseCommaSeparatedString(scanner.nextLine());
        System.out.print("Enter total papers: ");
        int totalPapers = Integer.parseInt(scanner.nextLine());

        Task task = createGradingTask(name, workHours, expertise, preference, timeOccupied, totalPapers);
        manager.registerTask(task, admin);
        System.out.println("GradingTask registered successfully!");
        task.printDetails();
    }

    private static String[] parseCommaSeparatedString(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new String[0];
        }
        return input.split("\\s*,\\s*");
    }
}