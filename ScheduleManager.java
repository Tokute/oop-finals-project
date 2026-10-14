import java.io.IOException;
import java.util.ArrayList;
import users.Admin;
import users.TeachingAssistant;
import users.User;
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
    private final ScheduleRepository repository;

    public ScheduleManager() throws IOException {
        this.repository = new ScheduleRepository(DATABASE_FILE);
        ScheduleRepository.Data data = repository.load();
        this.registeredUsers = data.users;
        this.registeredTeachingAssistants = data.teachingAssistants;
        this.registeredTasks = data.tasks;
        this.compatibleShifts = new ArrayList<String>();
    }

    public ScheduleManager(ArrayList<User> registeredUsers,
                          ArrayList<TeachingAssistant> registeredTeachingAssistants,
                          ArrayList<Task> registeredTasks) {
        this.repository = new ScheduleRepository(DATABASE_FILE);
        this.registeredUsers = registeredUsers;
        this.registeredTeachingAssistants = registeredTeachingAssistants;
        this.registeredTasks = registeredTasks;
        this.compatibleShifts = new ArrayList<String>();
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
        registerUser(newTA, requester);
    }

    public void registerTeachingAssistantAndSave(TeachingAssistant newTA, Admin requester) throws IOException {
        registerTeachingAssistant(newTA, requester);
        if (requester == null || newTA == null) {
            return;
        }

        try {
            saveToDatabase();
        } catch (IOException e) {
            registeredUsers.remove(newTA);
            registeredTeachingAssistants.remove(newTA);
            throw e;
        }
    }

    public boolean updateUser(User updatedUser, Admin requester) throws IOException {
        if (requester == null || updatedUser == null) {
            return false;
        }

        int userIndex = findUserIndex(updatedUser.getId());
        if (userIndex < 0) {
            return false;
        }

        User originalUser = registeredUsers.get(userIndex);
        if (originalUser.getClass() != updatedUser.getClass()) {
            return false;
        }

        if (originalUser instanceof TeachingAssistant) {
            int assistantIndex = registeredTeachingAssistants.indexOf(originalUser);
            if (assistantIndex >= 0) {
                registeredTeachingAssistants.set(assistantIndex, (TeachingAssistant) updatedUser);
            }
        }
        registeredUsers.set(userIndex, updatedUser);

        try {
            saveToDatabase();
        } catch (IOException e) {
            registeredUsers.set(userIndex, originalUser);
            if (originalUser instanceof TeachingAssistant) {
                int assistantIndex = registeredTeachingAssistants.indexOf(updatedUser);
                if (assistantIndex >= 0) {
                    registeredTeachingAssistants.set(assistantIndex, (TeachingAssistant) originalUser);
                }
            }
            throw e;
        }
        return true;
    }

    public boolean deleteUser(String userId, Admin requester) throws IOException {
        if (requester == null) {
            return false;
        }

        int userIndex = findUserIndex(userId);
        if (userIndex < 0) {
            return false;
        }

        User user = registeredUsers.get(userIndex);
        if (user instanceof Admin && countAdmins() <= 1) {
            throw new IllegalStateException("Cannot delete the last administrator.");
        }

        int assistantIndex = registeredTeachingAssistants.indexOf(user);
        registeredUsers.remove(userIndex);
        if (assistantIndex >= 0) {
            registeredTeachingAssistants.remove(assistantIndex);
        }

        try {
            saveToDatabase();
        } catch (IOException e) {
            registeredUsers.add(userIndex, user);
            if (assistantIndex >= 0) {
                registeredTeachingAssistants.add(assistantIndex, (TeachingAssistant) user);
            }
            throw e;
        }
        return true;
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

    public void registerTaskAndSave(Task newTask, Admin requester) throws IOException {
        registerTask(newTask, requester);
        if (requester == null || newTask == null) {
            return;
        }

        try {
            saveToDatabase();
        } catch (IOException e) {
            registeredTasks.remove(newTask);
            throw e;
        }
    }

    public boolean updateTask(Task updatedTask, Admin requester) throws IOException {
        if (requester == null || updatedTask == null) {
            return false;
        }

        int taskIndex = findTaskIndex(updatedTask.getId());
        if (taskIndex < 0 || registeredTasks.get(taskIndex).getClass() != updatedTask.getClass()) {
            return false;
        }

        Task originalTask = registeredTasks.set(taskIndex, updatedTask);
        try {
            saveToDatabase();
        } catch (IOException e) {
            registeredTasks.set(taskIndex, originalTask);
            throw e;
        }
        return true;
    }

    public boolean deleteTask(String taskId, Admin requester) throws IOException {
        if (requester == null) {
            return false;
        }

        int taskIndex = findTaskIndex(taskId);
        if (taskIndex < 0) {
            return false;
        }

        Task task = registeredTasks.remove(taskIndex);
        try {
            saveToDatabase();
        } catch (IOException e) {
            registeredTasks.add(taskIndex, task);
            throw e;
        }
        return true;
    }

    public void calculateAvailableShift() {
        try {
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
        } catch (Exception e) {
            System.err.println("Error calculating available shifts: " + e.getMessage());
            e.printStackTrace();
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
        return new ArrayList<>(this.compatibleShifts);
    }

    public ArrayList<User> getRegisteredUsers() {
        return new ArrayList<>(registeredUsers);
    }

    public ArrayList<Task> getRegisteredTasks() {
        return new ArrayList<>(registeredTasks);
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

    public void saveToDatabase() throws IOException {
        repository.save(registeredUsers, registeredTasks);
    }

    private int findUserIndex(String userId) {
        for (int i = 0; i < registeredUsers.size(); i++) {
            if (registeredUsers.get(i).getId().equals(userId)) {
                return i;
            }
        }
        return -1;
    }

    private int findTaskIndex(String taskId) {
        for (int i = 0; i < registeredTasks.size(); i++) {
            if (registeredTasks.get(i).getId().equals(taskId)) {
                return i;
            }
        }
        return -1;
    }

    private int countAdmins() {
        int count = 0;
        for (User user : registeredUsers) {
            if (user instanceof Admin) {
                count++;
            }
        }
        return count;
    }
}