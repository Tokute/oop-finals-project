package tasks;

public class GradingTask extends Task {
    private int totalPapers;

    public GradingTask(String name, String id, double workHours,
                String[] expertise, String[] preference, String[] timeOccupied, int totalPapers) {
        super(name, id, workHours, expertise, preference, timeOccupied);
        this.totalPapers = totalPapers;
    }

    @Override
    public void printDetails() {
        super.printDetails();
        System.out.printf("Total Papers: %d\n", totalPapers);
    }
}