package utils;

public class TimeUtils {
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