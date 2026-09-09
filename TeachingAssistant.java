public class TeachingAssistant extends User {
    private double workload;
    private final int WORKLOAD_MAX = 5; // This concept is still yet to be approved.
    private String[] expertise;    
    private String[] preferences;
    private String[] availableTime;
    
    public TeachingAssistant(String name, String password, String id, double workload, String[] expertise, String[] preferences, String[] availableTime) {
        super(name, password, id);
        this.availableTime = availableTime;
        this.workload = workload;
        this.preferences = preferences;
        this.expertise = expertise;
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

    public String[] getPreferences() {
        return this.preferences;
    }

    public void setPreferences(String[] preferences) {
        this.preferences = preferences;
    }

    public String[] getAvailableTime() {
        return this.availableTime;
    }

    public void setAvailableTime(String[] availableTime) {
        this.availableTime = availableTime;
    }

    public boolean isOverloaded() { // This concept is still yet to be approved.
        return (this.workload >= WORKLOAD_MAX);
    }

    @Override
    public void printDetails() {
        super.printDetails();
        System.out.printf("Available Time: %s\n", String.join(", ", this.availableTime));
        System.out.printf("Workload: %.2f\n", this.workload);
        System.out.printf("Preferences: %s\n", String.join(", ", preferences));
        System.out.printf("Expertise: %s\n", String.join(", ", expertise));
    }

    public String[][] getAttributes() {
        return new String[][]{this.expertise, this.preferences, this.availableTime};
    }
}
