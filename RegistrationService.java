import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import users.Admin;
import users.TeachingAssistant;
import tasks.Task;
import tasks.TeachingTask;
import tasks.GradingTask;
import utils.TimeUtils;
import utils.IDCreator;

public class RegistrationService {
    private final Scanner scanner;
    private final ScheduleManager manager;
    private final Admin admin;

    public RegistrationService(Scanner scanner, ScheduleManager manager, Admin admin) {
        this.scanner = scanner;
        this.manager = manager;
        this.admin = admin;
    }

    public void registerNewUserOrTask() {
        System.out.println();
        System.out.println("What would you like to register?");
        System.out.println("1. TeachingAssistant");
        System.out.println("2. TeachingTask");
        System.out.println("3. GradingTask");
        System.out.print("Enter your choice: ");

        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        try {
            switch (choice) {
                case 1:
                    registerTeachingAssistant();
                    break;
                case 2:
                    registerTeachingTask();
                    break;
                case 3:
                    registerGradingTask();
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    public void registerTeachingAssistant() {
        System.out.println();
        System.out.println("=== Register TeachingAssistant ===");

        while (true) {
            try {
                System.out.print("Enter name: ");
                String name = scanner.nextLine();
                System.out.print("Enter password: ");
                String password = scanner.nextLine();

                System.out.print("Enter expertise (comma-separated): ");
                String[] expertise = parseCommaSeparatedString(scanner.nextLine());
                System.out.print("Enter preferences (comma-separated): ");
                String[] preferences = parseCommaSeparatedString(scanner.nextLine());

                String[] timeDetails = readTimeDetails();
                TeachingAssistant ta = createTeachingAssistant(name, password, expertise, preferences, timeDetails);

                manager.registerTeachingAssistant(ta, admin);
                manager.saveToDatabase();
                System.out.println("TeachingAssistant registered successfully!");
                ta.printDetails();
                return;
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    public void registerTeachingTask() {
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
        String[] timeOccupied = readTimeDetails();
        System.out.print("Enter course type: ");
        String courseType = scanner.nextLine();

        Task task = createTeachingTask(name, workHours, expertise, preference, timeOccupied, courseType);
        registerTask(task, "TeachingTask");
    }

    public void registerGradingTask() {
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
        String[] timeOccupied = readTimeDetails();
        System.out.print("Enter total papers: ");
        int totalPapers = Integer.parseInt(scanner.nextLine());

        Task task = createGradingTask(name, workHours, expertise, preference, timeOccupied, totalPapers);
        registerTask(task, "GradingTask");
    }

    public static TeachingAssistant createTeachingAssistant(
            String name, String password, String[] expertise, String[] preferences, String[] timeDetails) {
        return new TeachingAssistant(
                name, password, IDCreator.createTeachingAssistantID(), expertise, preferences, timeDetails);
    }

    public static Task createTeachingTask(
            String name, double workHours, String[] expertise, String[] preference,
            String[] timeOccupied, String courseType) {
        return new TeachingTask(
                name, IDCreator.createTeachingTaskID(), workHours, expertise, preference, timeOccupied, courseType);
    }

    public static Task createGradingTask(
            String name, double workHours, String[] expertise, String[] preference,
            String[] timeOccupied, int totalPapers) {
        return new GradingTask(
                name, IDCreator.createGradingTaskID(), workHours, expertise, preference, timeOccupied, totalPapers);
    }

    private void registerTask(Task task, String taskType) {
        manager.registerTask(task, admin);
        manager.saveToDatabase();
        System.out.println(taskType + " registered successfully!");
        task.printDetails();
    }

    private String[] readTimeDetails() {
        List<String> timeDetails = new ArrayList<>();

        do {
            while (true) {
                try {
                    timeDetails.add(TimeUtils.readValidatedTimeDetail(scanner));
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid time detail: " + e.getMessage());
                }
            }
        } while (wantsAnotherTimeDetail());

        return timeDetails.toArray(new String[0]);
    }

    private boolean wantsAnotherTimeDetail() {
        while (true) {
            System.out.print("Do you want to add another time detail? (y/n): ");
            String answer = scanner.nextLine().trim();

            if (answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes")) {
                return true;
            }
            if (answer.equalsIgnoreCase("n") || answer.equalsIgnoreCase("no")) {
                return false;
            }

            System.out.println("Please answer yes or no.");
        }
    }

    private static String[] parseCommaSeparatedString(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new String[0];
        }
        return input.split("\\s*,\\s*");
    }
}
