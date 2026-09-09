public abstract class Task {
    private String name;
    private String id;
    private double workHours;
    private double stress;
    private double workload;
    private String[] expertise;
    private String[] preference;
    private String[] timeOccupied;

    public Task(String name, String id, double workHours, double stress,
                String[] expertise, String[] preference, String[] timeOccupied) {
        this.name = name;
        this.id = id;
        this.workHours = workHours;
        this.stress = stress;
        this.expertise = expertise;
        this.preference = preference;
        this.timeOccupied = timeOccupied;
        calculateWorkload();
    }

    public Task() {
    }

    public void printDetails() {
        System.out.printf("Name: %s\n", this.name);
        System.out.printf("ID: %s\n", this.id);
        System.out.printf("Work Hours: %.2f\n", this.workHours);
        System.out.printf("Stress: %.2f\n", this.stress);
        System.out.printf("Workload: %.2f\n", this.workload);
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

    private void calculateWorkload() {
        this.workload = (workHours * 0.5) + stress;
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
        calculateWorkload();
    }

    public double getStress() {
        return this.stress;
    }

    public void setStress(double stress) {
        this.stress = stress;
        calculateWorkload();
    }

    public double getWorkload() {
        return this.workload;
    }

    public void setWorkload(double workload) {
        this.workload = workload;
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