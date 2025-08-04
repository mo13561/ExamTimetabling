public class Swap extends Move { // a swap is simply two moves.
    private final Exam exam2;

    public Swap(Exam exam1, Exam exam2) {
        super(exam1, exam2.getTimeslot(), exam2.getRoom());
        this.exam2 = exam2;
    }

    public Exam getExam2() {
        return exam2;
    }
}
