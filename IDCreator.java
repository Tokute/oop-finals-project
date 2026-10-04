public class IDCreator {
    private static int adminCounter = 1;
    private static int taCounter = 1;
    private static int teachingTaskCounter = 1;
    private static int gradingTaskCounter = 1;

    public static String createAdminID() {
        return String.format("A-%03d", adminCounter++);
    }

    public static String createTeachingAssistantID() {
        return String.format("TA-%03d", taCounter++);
    }

    public static String createTeachingTaskID() {
        return String.format("TT-%03d", teachingTaskCounter++);
    }

    public static String createGradingTaskID() {
        return String.format("GT-%03d", gradingTaskCounter++);
    }

    // Reset counters (useful for testing)
    public static void reset() {
        adminCounter = 1;
        taCounter = 1;
        teachingTaskCounter = 1;
        gradingTaskCounter = 1;
    }
}