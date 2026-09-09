public class TeachingTask extends Task {
    private String subject;

    public TeachingTask(String name, String id, double workHours, int workload,
                String[] expertise, String[] preference, String[] timeOccupied,
                String subject) {
        super(name, id, workHours, 1.5, expertise, preference, timeOccupied);
        this.subject = subject;
    }

    @Override
    public void printDetails() {
        super.printDetails();
        System.out.printf("Subject: %s\n", this.subject);
    }
}
