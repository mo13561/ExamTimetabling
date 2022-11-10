public class Move {
    private final Timeslot timeslot2;
    private final Room room2;
    private final Exam exam;
    private double cost;

    public Move(Exam exam, Timeslot timeslot2, Room room2) {
        this.timeslot2 = timeslot2;
        this.room2 = room2;
        this.exam = exam;
    }

    public Move(Exam exam, Timeslot timeslot2, Room room2, double cost) {
        this.timeslot2 = timeslot2;
        this.room2 = room2;
        this.exam = exam;
        this.cost = cost;
    }

    public Timeslot getTimeslot2() {
        return timeslot2;
    }

    public Exam getExam() {
        return exam;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public Room getRoom2() {
        return room2;
    }
}
