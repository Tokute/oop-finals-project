import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ScheduleManager manager = new ScheduleManager();
        Admin admin = new Admin("Admin User", "admin123", "A-001");
        manager.registerUser(admin, admin);

        while (true) {
            System.out.println();
            System.out.println("========== Main Menu ==========");
            System.out.println("1. Register sample TeachingAssistant");
            System.out.println("2. Register sample TeachingTask");
            System.out.println("3. Register sample GradingTask");
            System.out.println("4. View all users");
            System.out.println("5. View all tasks");
            System.out.println("6. Check compatible shifts");
            System.out.println("7. Exit");
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
                    TeachingAssistant ta = createTeachingAssistant();
                    manager.registerTeachingAssistant(ta, admin);
                    System.out.println("TeachingAssistant registered successfully.");
                    ta.printDetails();
                    break;

                case 2:
                    Task teachingTask = new TeachingTask(
                            "Java Tutoring",
                            "TT-01",
                            6.0,
                            2,
                            new String[]{"Java", "OOP"},
                            new String[]{"Morning"},
                            new String[]{"Tue 09:00-15:00", "Thu 09:00-15:00"},
                            "Programming"
                    );
                    manager.registerTask(teachingTask, admin);
                    System.out.println("TeachingTask registered successfully.");
                    teachingTask.printDetails();
                    break;

                case 3:
                    Task gradingTask = new GradingTask(
                            "Essay Grading",
                            "GT-01",
                            5.0,
                            1.2,
                            4,
                            new String[]{"Writing", "Assessment"},
                            new String[]{"Night"},
                            new String[]{"Fri 13:00-18:00"},
                            40
                    );
                    manager.registerTask(gradingTask, admin);
                    System.out.println("GradingTask registered successfully.");
                    gradingTask.printDetails();
                    break;

                case 4:
                    manager.viewUsers();
                    break;

                case 5:
                    manager.viewTasks();
                    break;

                case 6:
                    manager.calculateAvailableShift();
                    System.out.println("Compatible shifts:");
                    if (manager.getCompatibleShifts().isEmpty()) {
                        System.out.println("No compatible assignment found.");
                    } else {
                        for (String shift : manager.getCompatibleShifts()) {
                            System.out.println(shift);
                        }
                    }
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

    private static TeachingAssistant createTeachingAssistant() {
        return new TeachingAssistant(
                "Alice",
                "pass123",
                "TA-001",
                2,
                new String[]{"Java", "Math", "Algorithms", "Writing"},
                new String[]{"Morning", "Flexible"},
                new String[]{"Tue 00:00-23:59", "Wed 00:00-23:59", "Fri 00:00-23:59"}
        );
    }
}
