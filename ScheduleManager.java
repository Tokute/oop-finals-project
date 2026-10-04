import java.util.ArrayList;
import users.User;
import users.Admin;
import users.TeachingAssistant;
import tasks.Task;
import utils.TimeUtils;

public class ScheduleManager {
    private ArrayList<User> registeredUsers;
    private ArrayList<TeachingAssistant> registeredTeachingAssistants;
    private ArrayList<Task> registeredTasks;
    private ArrayList<String> compatibleShifts;

    public ScheduleManager() {
        this.registeredUsers = new ArrayList<User>();
        this.registeredTeachingAssistants = new ArrayList<TeachingAssistant>();
        this.registeredTasks = new ArrayList<Task>();
        this.compatibleShifts = new ArrayList<String>();
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
}