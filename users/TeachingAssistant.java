package users;

public class TeachingAssistant extends User {
    private String[] expertise;
    private String[] preferences;
    private String[] availableTime;

    public TeachingAssistant(String name, String password, String id, String[] expertise, String[] preferences, String[] availableTime) {
        super(name, password, id);
        this.availableTime = availableTime;
        this.expertise = expertise;
        this.preferences = preferences;
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

    @Override
    public void printDetails() {
        super.printDetails();
        System.out.printf("Available Time: %s\n", String.join(", ", this.availableTime));
        System.out.printf("Preferences: %s\n", String.join(", ", preferences));
        System.out.printf("Expertise: %s\n", String.join(", ", expertise));
    }

    public String[][] getAttributes() {
        return new String[][]{this.expertise, this.preferences, this.availableTime};
    }
}