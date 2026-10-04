import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import users.User;
import users.Admin;
import users.TeachingAssistant;
import tasks.Task;
import tasks.TeachingTask;
import tasks.GradingTask;
import utils.TimeUtils;

public class ScheduleManager {
    public static final String DATABASE_FILE = "database.txt";
    private ArrayList<User> registeredUsers;
    private ArrayList<TeachingAssistant> registeredTeachingAssistants;
    private ArrayList<Task> registeredTasks;
    private ArrayList<String> compatibleShifts;

    public ScheduleManager() {
        this.registeredUsers = new ArrayList<User>();
        this.registeredTeachingAssistants = new ArrayList<TeachingAssistant>();
        this.registeredTasks = new ArrayList<Task>();
        this.compatibleShifts = new ArrayList<String>();
        loadFromDatabase();
    }

    public ScheduleManager(ArrayList<User> registeredUsers,
                          ArrayList<TeachingAssistant> registeredTeachingAssistants,
                          ArrayList<Task> registeredTasks) {
        this.registeredUsers = registeredUsers;
        this.registeredTeachingAssistants = registeredTeachingAssistants;
        this.registeredTasks = registeredTasks;
    }

    public void viewUsers() {
        if (this.registeredUsers == null || this.registeredUsers.size() == 0) {
            System.out.println("No users registered.");
            return;
        }

        System.out.println("=== Registered Users ===");
        for (int i = 0; i < this.registeredUsers.size(); i++) {
            if (this.registeredUsers.get(i) != null) {
                System.out.println("========================================");
                System.out.println("User " + (i + 1) + ":");
                this.registeredUsers.get(i).printDetails();
                System.out.println();
            }
        }
        System.out.println("========================================");
    }

    public void viewTasks() {
        if (this.registeredTasks == null || this.registeredTasks.size() == 0) {
            System.out.println("No tasks registered.");
            return;
        }

        System.out.println("=== Registered Tasks ===");
        for (int i = 0; i < this.registeredTasks.size(); i++) {
            if (this.registeredTasks.get(i) != null) {
                System.out.println("========================================");
                System.out.println("Task " + (i + 1) + ":");
                this.registeredTasks.get(i).printDetails();
                System.out.println();
            }
        }
        System.out.println("========================================");
    }

    public void registerUser(User newUser, Admin requester) {
        if (requester == null) {
            return;
        }

        if (newUser == null) {
            return;
        }

        registeredUsers.add(newUser);

        if (newUser instanceof TeachingAssistant) {
            registeredTeachingAssistants.add((TeachingAssistant) newUser);
        }
    }

    public void registerTeachingAssistant(TeachingAssistant newTA, Admin requester) {
        if (requester == null) {
            return;
        }

        if (newTA == null) {
            return;
        }

        registeredUsers.add(newTA);
        registeredTeachingAssistants.add(newTA);
    }

    public void registerTask(Task newTask, Admin requester) {
        if (requester == null) {
            return;
        }

        if (newTask == null) {
            return;
        }

        registeredTasks.add(newTask);
    }

    public void calculateAvailableShift() {
        if (registeredTeachingAssistants.size() == 0 || registeredTasks.size() == 0) {
            return;
        }

        compatibleShifts.clear();

        for (TeachingAssistant ta : registeredTeachingAssistants) {
            for (Task task : registeredTasks) {
                if (!task.isCompatible(ta)) {
                    continue;
                }

                // Check preference compatibility
                if (!isPreferenceCompatible(ta, task)) {
                    continue;
                }

                ArrayList<String> matchingTimes = evaluateTimeOverlap(ta, task);
                if (matchingTimes == null || matchingTimes.isEmpty()) {
                    continue;
                }

                // Aggregate matching times into a single string
                String taName = ta.getName();
                String taskName = task.getName();
                String timesList = String.join(", ", matchingTimes);
                compatibleShifts.add(taName + " can take " + taskName + " at:\t[" + timesList + "]");
            }
        }
    }

    private static boolean isPreferenceCompatible(TeachingAssistant ta, Task task) {
        String[] taPreferences = ta.getPreferences();
        String[] taskPreferences = task.getPreference();

        // If either array is null or empty, consider it compatible (no preference constraint)
        if (taPreferences == null || taskPreferences == null ||
            taPreferences.length == 0 || taskPreferences.length == 0) {
            return true;
        }

        // Check if any TA preference matches any task preference
        for (String taPref : taPreferences) {
            for (String taskPref : taskPreferences) {
                if (taPref != null && taskPref != null &&
                    taPref.equalsIgnoreCase(taskPref)) {
                    return true;
                }
            }
        }

        return false;
    }

    public ArrayList<String> getCompatibleShifts() {
        return this.compatibleShifts;
    }

    public ArrayList<User> getRegisteredUsers() {
        return new ArrayList<>(registeredUsers);
    }

    public ArrayList<String> evaluateTimeOverlap(TeachingAssistant ta, Task task) {
        if (ta == null) {
            return new ArrayList<>();
        }
        if (task == null) {
            return new ArrayList<>();
        }

        ArrayList<String> overlappedTimes = new ArrayList<>();

        String[] taTimeDetails = ta.getAttributes()[2];
        String[] taskTimeDetails = task.taskRequirement()[2];

        for (String availableTime : taTimeDetails) {
            for (String requiredTime : taskTimeDetails) {
                if (TimeUtils.overlaps(availableTime, requiredTime))
                    overlappedTimes.add(requiredTime);
            }
        }

        return overlappedTimes;
    }

    /**
     * Load data from the database file on startup.
     */
    private void loadFromDatabase() {
        java.io.File file = new java.io.File(DATABASE_FILE);
        if (!file.exists()) {
            // Database file doesn't exist yet, start with empty lists
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue; // Skip empty lines and comments
                }

                String[] parts = line.split("\\|", -1); // -1 to keep trailing empty strings
                if (parts.length < 2) {
                    continue; // Invalid line
                }

                String type = parts[0];
                try {
                    switch (type) {
                        case "ADMIN":
                            if (parts.length >= 4) {
                                String name = parts[1];
                                String password = parts[2];
                                String id = parts[3];
                                Admin admin = new Admin(name, password, id);
                                registeredUsers.add(admin);
                                // Note: Admins are not added to registeredTeachingAssistants
                            }
                            break;

                        case "TEACHING_ASSISTANT":
                            if (parts.length >= 7) {
                                String name = parts[1];
                                String password = parts[2];
                                String id = parts[3];
                                String[] expertise = parseCsvString(parts[4]);
                                String[] preferences = parseCsvString(parts[5]);
                                String[] timeDetails = parseCsvString(parts[6]);
                                TeachingAssistant ta = new TeachingAssistant(name, password, id, expertise, preferences, timeDetails);
                                registeredUsers.add(ta);
                                registeredTeachingAssistants.add(ta);
                            }
                            break;

                        case "TEACHING_TASK":
                            if (parts.length >= 8) {
                                String name = parts[1];
                                String id = parts[2];
                                double workHours = Double.parseDouble(parts[3]);
                                String[] expertise = parseCsvString(parts[4]);
                                String[] preference = parseCsvString(parts[5]);
                                String[] timeOccupied = parseCsvString(parts[6]);
                                String courseType = parts[7];
                                Task task = new TeachingTask(name, id, workHours, expertise, preference, timeOccupied, courseType);
                                registeredTasks.add(task);
                            }
                            break;

                        case "GRADING_TASK":
                            if (parts.length >= 8) {
                                String name = parts[1];
                                String id = parts[2];
                                double workHours = Double.parseDouble(parts[3]);
                                String[] expertise = parseCsvString(parts[4]);
                                String[] preference = parseCsvString(parts[5]);
                                String[] timeOccupied = parseCsvString(parts[6]);
                                int totalPapers = Integer.parseInt(parts[7]);
                                Task task = new GradingTask(name, id, workHours, expertise, preference, timeOccupied, totalPapers);
                                registeredTasks.add(task);
                            }
                            break;

                        default:
                            // Unknown type, skip
                            break;
                    }
                } catch (Exception e) {
                    // Skip malformed lines but continue processing
                    System.err.println("Warning: Skipping malformed line in database: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading from database: " + e.getMessage());
        }
    }

    /**
     * Save current state to the database file.
     */
    public void saveToDatabase() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(DATABASE_FILE))) {
            // Save Admin users
            for (User user : registeredUsers) {
                if (user instanceof Admin) {
                    Admin admin = (Admin) user;
                    bw.write("ADMIN|" +
                            admin.getName() + "|" +
                            admin.getPassword(admin) + "|" +
                            admin.getId());
                    bw.newLine();
                }
            }

            // Save TeachingAssistant users
            for (User user : registeredUsers) {
                if (user instanceof TeachingAssistant) {
                    TeachingAssistant ta = (TeachingAssistant) user;
                    bw.write("TEACHING_ASSISTANT|" +
                            ta.getName() + "|" +
                            ta.getPassword(ta) + "|" +
                            ta.getId() + "|" +
                            toCsvString(ta.getExpertise()) + "|" +
                            toCsvString(ta.getPreferences()) + "|" +
                            toCsvString(ta.getAttributes()[2])); // timeDetails is at index 2
                    bw.newLine();
                }
            }

            // Save TeachingTask objects
            for (Task task : registeredTasks) {
                if (task instanceof TeachingTask) {
                    TeachingTask tt = (TeachingTask) task;
                    bw.write("TEACHING_TASK|" +
                            tt.getName() + "|" +
                            tt.getId() + "|" +
                            tt.getWorkHours() + "|" +
                            toCsvString(tt.getExpertise()) + "|" +
                            toCsvString(tt.getPreference()) + "|" +
                            toCsvString(tt.taskRequirement()[2]) + "|" + // timeOccupied is at index 2
                            tt.getSubject());
                    bw.newLine();
                }
            }

            // Save GradingTask objects
            for (Task task : registeredTasks) {
                if (task instanceof GradingTask) {
                    GradingTask gt = (GradingTask) task;
                    bw.write("GRADING_TASK|" +
                            gt.getName() + "|" +
                            gt.getId() + "|" +
                            gt.getWorkHours() + "|" +
                            toCsvString(gt.getExpertise()) + "|" +
                            toCsvString(gt.getPreference()) + "|" +
                            toCsvString(gt.taskRequirement()[2]) + "|" + // timeOccupied is at index 2
                            gt.getTotalPapers());
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving to database: " + e.getMessage());
        }
    }

    /**
     * Convert an array to a comma-separated string.
     * Null arrays become empty strings.
     */
    private String toCsvString(String[] array) {
        if (array == null) {
            return "";
        }
        return String.join(",", array);
    }

    /**
     * Parse a comma-separated string into an array.
     * Empty strings become null arrays.
     */
    private String[] parseCsvString(String csv) {
        if (csv == null || csv.isEmpty()) {
            return new String[0];
        }
        return csv.split(",");
    }
}