import java.util.ArrayList;

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
            System.out.println("You must be an admin to add a user.");
            return;
        }

        if (newUser == null) {
            System.out.println("Error. User is empty.");
            return;
        }

        registeredUsers.add(newUser);

        if (newUser instanceof TeachingAssistant) {
            registeredTeachingAssistants.add((TeachingAssistant) newUser);
        }
    }

    public void registerTeachingAssistant(TeachingAssistant newTA, Admin requester) {
        if (requester == null) {
            System.out.println("You must be an admin to add a teaching assistant.");
            return;
        }

        if (newTA == null) {
            System.out.println("Error. TeachingAssistant is empty.");
            return;
        }

        registeredUsers.add(newTA);
        registeredTeachingAssistants.add(newTA);
    }

    public void registerTask(Task newTask, Admin requester) {
        if (requester == null) {
            System.out.println("You must be an admin to add a task.");
            return;
        }

        if (newTask == null) {
            System.out.println("Error. Task is empty.");
            return;
        }

        registeredTasks.add(newTask);
    }

    public void calculateAvailableShift() {
        if (registeredTeachingAssistants.size() == 0 || registeredTasks.size() == 0) {
            System.out.println("There are no teaching assistants or tasks to compare.");
            return;
        }

        compatibleShifts.clear();

        for (TeachingAssistant ta : registeredTeachingAssistants) {
            for (Task task : registeredTasks) {
                if (!task.isCompatible(ta)) {
                    continue;
                }

                ArrayList<String> matchingTimes = evaluateTimeOverlap(ta, task);
                if (matchingTimes == null || matchingTimes.isEmpty()) {
                    continue;
                }

                String taName = ta.getName();
                String taskName = task.getName();
                compatibleShifts.add(taName + " can take " + taskName + " at " + matchingTimes);
            }
        }
    }

    public ArrayList<String> getCompatibleShifts() {
        return this.compatibleShifts;
    }

    public ArrayList<String> evaluateTimeOverlap(TeachingAssistant ta, Task task) {
        if (ta == null) {
            System.out.println("Teaching Assistant User could not be found.");
            return null;
        }
        if (task == null) {
            System.out.println("Task could not be found.");
            return null;
        }

        ArrayList<String> overlappedTimes = new ArrayList<>();

        String[] taTimeDetails = ta.getAttributes()[2];
        String[] taskTimeDetails = task.taskRequirement()[2];

        for (String availableTime : taTimeDetails) {
            for (String requiredTime : taskTimeDetails) {
                if (timeOverlap(availableTime, requiredTime))
                    overlappedTimes.add(requiredTime);
            }
        }

        return overlappedTimes;
    }

    public boolean timeOverlap(String availableTimeDetails, String requiredTimeDetails) {   // helper function for calculating time overlap.
        String availableDay = availableTimeDetails.substring(0, 3);
        String requiredDay = requiredTimeDetails.substring(0, 3);

        if (!availableDay.equalsIgnoreCase(requiredDay)) {
            return false;
        }

        String[] availableTime = availableTimeDetails.substring(4).split("-");
        String[] requiredTime = requiredTimeDetails.substring(4).split("-");

        int availableStart = toMinutes(availableTime[0]);
        int availableEnd = toMinutes(availableTime[1]);

        int requiredStart = toMinutes(requiredTime[0]);
        int requiredEnd = toMinutes(requiredTime[1]);

        return !(requiredEnd <= availableStart || requiredStart >= availableEnd);
    }

    public int toMinutes(String time) { // helper function to use on time-related comparison.
        String[] timeGiven = time.split(":");
        int hour = Integer.parseInt(timeGiven[0]);
        int minutes = Integer.parseInt(timeGiven[1]);

        return hour * 60 + minutes;
    }
}
