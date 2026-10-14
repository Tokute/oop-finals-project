package utils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class TimeUtils {
    public static String readValidatedTimeDetail(Scanner scanner) {
        System.out.println("Enter time details (format: Day HH:MM-HH:MM)");
        System.out.print("Day: ");
        String day = scanner.nextLine().trim();
        System.out.print("Start time (HH:MM): ");
        String startTime = scanner.nextLine().trim();
        System.out.print("End time (HH:MM): ");
        String endTime = scanner.nextLine().trim();

        return normalizeTimeDetail(day, startTime, endTime);
    }

    public static String[] readValidatedTimeSlots(Scanner scanner, String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            return new String[0];
        }

        String[] rawSlots = input.split("\\s*,\\s*");
        List<String> validSlots = new ArrayList<>();

        for (String slot : rawSlots) {
            if (!slot.trim().isEmpty()) {
                validSlots.add(normalizeTimeDetailFromString(slot));
            }
        }

        return validSlots.toArray(new String[0]);
    }

    public static String normalizeTimeDetail(String day, String startTime, String endTime) {
        String normalizedDay = normalizeDay(day);
        LocalTime start = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm"));
        LocalTime end = LocalTime.parse(endTime, DateTimeFormatter.ofPattern("HH:mm"));

        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("End time must be after start time.");
        }

        return normalizedDay + " " + start.format(DateTimeFormatter.ofPattern("HH:mm"))
                + "-" + end.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public static String normalizeTimeDetailFromString(String timeDetail) {
        String trimmed = timeDetail.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Time detail cannot be empty.");
        }

        String[] parts = trimmed.split("\\s+");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid time detail format. Use: Day HH:MM-HH:MM");
        }

        String day = parts[0];
        String[] timeRange = parts[1].split("-");
        if (timeRange.length != 2) {
            throw new IllegalArgumentException("Invalid time range format. Use: HH:MM-HH:MM");
        }

        return normalizeTimeDetail(day, timeRange[0], timeRange[1]);
    }

    public static String normalizeDay(String day) {
        String normalized = day.trim().toLowerCase(Locale.ROOT);

        switch (normalized) {
            case "mon":
            case "monday":
                return "Mon";
            case "tue":
            case "tuesday":
                return "Tue";
            case "wed":
            case "wednesday":
                return "Wed";
            case "thu":
            case "thursday":
                return "Thu";
            case "fri":
            case "friday":
                return "Fri";
            case "sat":
            case "saturday":
                return "Sat";
            case "sun":
            case "sunday":
                return "Sun";
            default:
                throw new IllegalArgumentException("Invalid day. Use names such as Mon, Tue, Wed, Thu, Fri, Sat, Sun.");
        }
    }

    public static boolean overlaps(String availableTimeDetails, String requiredTimeDetails) {
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

    public static int toMinutes(String time) {
        String[] timeGiven = time.split(":");
        int hour = Integer.parseInt(timeGiven[0]);
        int minutes = Integer.parseInt(timeGiven[1]);

        return hour * 60 + minutes;
    }
}