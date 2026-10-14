package utils;

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

    public static void observeExistingId(String id) {
        if (id == null) {
            return;
        }

        int separatorIndex = id.lastIndexOf('-');
        if (separatorIndex < 0 || separatorIndex == id.length() - 1) {
            return;
        }

        int existingNumber;
        try {
            existingNumber = Integer.parseInt(id.substring(separatorIndex + 1));
        } catch (NumberFormatException e) {
            return;
        }

        if (existingNumber < 1) {
            return;
        }

        String prefix = id.substring(0, separatorIndex);
        switch (prefix) {
            case "A":
                adminCounter = Math.max(adminCounter, existingNumber + 1);
                break;
            case "TA":
                taCounter = Math.max(taCounter, existingNumber + 1);
                break;
            case "TT":
                teachingTaskCounter = Math.max(teachingTaskCounter, existingNumber + 1);
                break;
            case "GT":
                gradingTaskCounter = Math.max(gradingTaskCounter, existingNumber + 1);
                break;
            default:
                break;
        }
    }

    // Reset counters (useful for testing)
    public static void reset() {
        adminCounter = 1;
        taCounter = 1;
        teachingTaskCounter = 1;
        gradingTaskCounter = 1;
    }
}
