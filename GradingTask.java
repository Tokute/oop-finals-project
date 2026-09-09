public class GradingTask extends Task {
    private int totalPapers;

    public GradingTask(String name, String id, double workHours, double stress, int workload,
                String[] expertise, String[] preference, String[] timeOccupied, int totalPapers) {
        super(name, id, workHours, 1.2, expertise, preference, timeOccupied);
        this.totalPapers = totalPapers; 
    }

    @Override 
    public void printDetails() {
        super.printDetails();
        System.out.printf("Total Papers: %d\n", totalPapers);
    }
}
