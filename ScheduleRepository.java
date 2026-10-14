import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import tasks.GradingTask;
import tasks.Task;
import tasks.TeachingTask;
import users.Admin;
import users.TeachingAssistant;
import users.User;
import utils.IDCreator;

public class ScheduleRepository {
    private final File databaseFile;

    public ScheduleRepository(String databasePath) {
        this.databaseFile = new File(databasePath);
    }

    public Data load() throws IOException {
        ArrayList<User> users = new ArrayList<>();
        ArrayList<TeachingAssistant> teachingAssistants = new ArrayList<>();
        ArrayList<Task> tasks = new ArrayList<>();
        List<String> loadedIds = new ArrayList<>();

        if (!databaseFile.exists()) {
            return new Data(users, teachingAssistants, tasks);
        }

        int lineNumber = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(databaseFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] fields = line.split("\\|", -1);
                String type = fields[0];
                try {
                    switch (type) {
                        case "ADMIN":
                            requireFieldCount(fields, 4, lineNumber);
                            String adminId = fields[3];
                            loadedIds.add(adminId);
                            users.add(new Admin(fields[1], fields[2], adminId));
                            break;
                        case "TEACHING_ASSISTANT":
                            requireFieldCount(fields, 7, lineNumber);
                            String taId = fields[3];
                            loadedIds.add(taId);
                            TeachingAssistant ta = new TeachingAssistant(
                                    fields[1], fields[2], taId, parseCsvString(fields[4]),
                                    parseCsvString(fields[5]), parseCsvString(fields[6]));
                            users.add(ta);
                            teachingAssistants.add(ta);
                            break;
                        case "TEACHING_TASK":
                            requireFieldCount(fields, 8, lineNumber);
                            String teachingTaskId = fields[2];
                            loadedIds.add(teachingTaskId);
                            tasks.add(new TeachingTask(
                                    fields[1], teachingTaskId, Double.parseDouble(fields[3]),
                                    parseCsvString(fields[4]), parseCsvString(fields[5]),
                                    parseCsvString(fields[6]), fields[7]));
                            break;
                        case "GRADING_TASK":
                            requireFieldCount(fields, 8, lineNumber);
                            String gradingTaskId = fields[2];
                            loadedIds.add(gradingTaskId);
                            tasks.add(new GradingTask(
                                    fields[1], gradingTaskId, Double.parseDouble(fields[3]),
                                    parseCsvString(fields[4]), parseCsvString(fields[5]),
                                    parseCsvString(fields[6]), Integer.parseInt(fields[7])));
                            break;
                        default:
                            throw new IOException("Unknown record type '" + type + "' at line " + lineNumber + ".");
                    }
                } catch (RuntimeException e) {
                    throw new IOException("Malformed database record at line " + lineNumber + ": " + line, e);
                }
            }
        }

        for (String id : loadedIds) {
            IDCreator.observeExistingId(id);
        }
        return new Data(users, teachingAssistants, tasks);
    }

    public void save(ArrayList<User> users, ArrayList<Task> tasks) throws IOException {
        File absoluteDatabaseFile = databaseFile.getAbsoluteFile();
        File directory = absoluteDatabaseFile.getParentFile();
        File temporaryFile = File.createTempFile(absoluteDatabaseFile.getName(), ".tmp", directory);
        File backupFile = null;

        try {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(temporaryFile))) {
                for (User user : users) {
                    if (user instanceof Admin) {
                        Admin admin = (Admin) user;
                        writer.write("ADMIN|" + admin.getName() + "|" + admin.getPassword(admin)
                                + "|" + admin.getId());
                        writer.newLine();
                    }
                }

                for (User user : users) {
                    if (user instanceof TeachingAssistant) {
                        TeachingAssistant ta = (TeachingAssistant) user;
                        writer.write("TEACHING_ASSISTANT|" + ta.getName() + "|" + ta.getPassword(ta)
                                + "|" + ta.getId() + "|" + toCsvString(ta.getExpertise())
                                + "|" + toCsvString(ta.getPreferences())
                                + "|" + toCsvString(ta.getAttributes()[2]));
                        writer.newLine();
                    }
                }

                for (Task task : tasks) {
                    if (task instanceof TeachingTask) {
                        TeachingTask teachingTask = (TeachingTask) task;
                        writer.write("TEACHING_TASK|" + teachingTask.getName() + "|" + teachingTask.getId()
                                + "|" + teachingTask.getWorkHours() + "|" + toCsvString(teachingTask.getExpertise())
                                + "|" + toCsvString(teachingTask.getPreference())
                                + "|" + toCsvString(teachingTask.taskRequirement()[2])
                                + "|" + teachingTask.getSubject());
                        writer.newLine();
                    }
                }

                for (Task task : tasks) {
                    if (task instanceof GradingTask) {
                        GradingTask gradingTask = (GradingTask) task;
                        writer.write("GRADING_TASK|" + gradingTask.getName() + "|" + gradingTask.getId()
                                + "|" + gradingTask.getWorkHours() + "|" + toCsvString(gradingTask.getExpertise())
                                + "|" + toCsvString(gradingTask.getPreference())
                                + "|" + toCsvString(gradingTask.taskRequirement()[2])
                                + "|" + gradingTask.getTotalPapers());
                        writer.newLine();
                    }
                }
            }

            if (absoluteDatabaseFile.exists()) {
                backupFile = File.createTempFile(absoluteDatabaseFile.getName(), ".bak", directory);
                if (!backupFile.delete()) {
                    throw new IOException("Could not prepare a backup file for "
                            + absoluteDatabaseFile.getName() + ".");
                }
                if (!absoluteDatabaseFile.renameTo(backupFile)) {
                    throw new IOException("Could not move the existing database to a backup file.");
                }
            }

            if (!temporaryFile.renameTo(absoluteDatabaseFile)) {
                IOException saveFailure = new IOException("Could not replace the database file.");
                if (backupFile != null && !backupFile.renameTo(absoluteDatabaseFile)) {
                    saveFailure.addSuppressed(
                            new IOException("Could not restore the previous database from its backup."));
                }
                throw saveFailure;
            }

            if (backupFile != null && !backupFile.delete()) {
                System.err.println("Warning: Saved database, but could not remove backup file: "
                        + backupFile.getAbsolutePath());
            }
        } finally {
            if (temporaryFile.exists() && !temporaryFile.delete()) {
                System.err.println("Warning: Could not remove temporary database file: "
                        + temporaryFile.getAbsolutePath());
            }
        }
    }

    private static void requireFieldCount(String[] fields, int expectedCount, int lineNumber) throws IOException {
        if (fields.length != expectedCount) {
            throw new IOException("Expected " + expectedCount + " fields at line " + lineNumber
                    + ", but found " + fields.length + ".");
        }
    }

    private static String toCsvString(String[] values) {
        return values == null ? "" : String.join(",", values);
    }

    private static String[] parseCsvString(String csv) {
        if (csv == null || csv.isEmpty()) {
            return new String[0];
        }
        return csv.split(",");
    }

    public static final class Data {
        public final ArrayList<User> users;
        public final ArrayList<TeachingAssistant> teachingAssistants;
        public final ArrayList<Task> tasks;

        private Data(ArrayList<User> users, ArrayList<TeachingAssistant> teachingAssistants,
                     ArrayList<Task> tasks) {
            this.users = users;
            this.teachingAssistants = teachingAssistants;
            this.tasks = tasks;
        }
    }
}
