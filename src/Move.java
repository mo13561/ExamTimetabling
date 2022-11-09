public class Move {
    private final Timeslot from;
    private final Timeslot to;
    private final Exam exam;
    private int cost;

    public Move(Timeslot from, Timeslot to, Exam exam) {
        this.from = from;
        this.to = to;
        this.exam = exam;
    }

    public Move(Timeslot from, Timeslot to, Exam exam, int cost) {
        this.from = from;
        this.to = to;
        this.exam = exam;
        this.cost = cost;
    }

    public Timeslot getFrom() {
        return from;
    }

    public Timeslot getTo() {
        return to;
    }

    public Exam getExam() {
        return exam;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
}
