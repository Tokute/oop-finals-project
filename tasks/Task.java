package tasks;

import users.TeachingAssistant;

public abstract class Task {
    private String name;
    private String id;
    private double workHours;
    private String[] expertise;
    private String[] preference;
    private String[] timeOccupied;

    public Task(String name, String id, double workHours,
                String[] expertise, String[] preference, String[] timeOccupied) {
        this.name = name;
        this.id = id;
        this.workHours = workHours;
        this.expertise = expertise;
        this.preference = preference;
        this.timeOccupied = timeOccupied;
    }

    public Task() {
    }

    public void printDetails() {
        System.out.printf("\nName: %s\n", this.name);
        System.out.printf("ID: %s\n", this.id);
        System.out.printf("Work Hours: %.2f\n", this.workHours);
        System.out.printf("Expertise: %s\n", String.join(", ", this.expertise));
        System.out.printf("Preference: %s\n", String.join(", ", this.preference));
        System.out.printf("Time Occupied: %s\n", String.join(", ", this.timeOccupied));
    }

    public String[][] taskRequirement() {
        return new String[][]{this.expertise, this.preference, this.timeOccupied};
    } // returns a 2D String Array. 0 is expertise, 1 is preference, 2 is timeOccupied.

    public boolean isCompatible(TeachingAssistant ta) {
        if (ta == null || this.expertise == null) {
            return false;
        }

        String[] taExpertise = ta.getAttributes()[0];
        if (taExpertise == null) {
            return false;
        }

        for (String requiredSkill : this.expertise) {
            for (String taSkill : taExpertise) {
                if (requiredSkill != null && taSkill != null &&
                    requiredSkill.equalsIgnoreCase(taSkill)) {
                    return true;
                }
            }
        }

        return false;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getWorkHours() {
        return this.workHours;
    }

    public void setWorkHours(double workHours) {
        this.workHours = workHours;
    }

    public String[] getExpertise() {
        return this.expertise;
    }

    public void setExpertise(String[] expertise) {
        this.expertise = expertise;
    }

    public String[] getPreference() {
        return this.preference;
    }

    public void setPreference(String[] preference) {
        this.preference = preference;
    }

    public String[] getTimeOccupied() {
        return this.timeOccupied;
    }

    public void setTimeOccupied(String[] timeOccupied) {
        this.timeOccupied = timeOccupied;
    }
}