public class Move {
    protected final Exam exam;
    protected final Timeslot timeslotFrom;
    protected final Timeslot timeslotTo;
    protected final Room roomFrom;
    protected final Room roomTo;

    public Move(Exam exam, Timeslot timeslotTo, Room roomTo) {
        this.exam = exam;
        this.roomFrom = exam.getRoom();
        this.timeslotFrom = exam.getTimeslot();
        this.roomTo = roomTo;
        this.timeslotTo = timeslotTo;
    }

    public Exam getExam() {
        return exam;
    }

    public Timeslot getTimeslotFrom() {
        return timeslotFrom;
    }

    public Timeslot getTimeslotTo() {
        return timeslotTo;
    }

    public Room getRoomFrom() {
        return roomFrom;
    }

    public Room getRoomTo() {
        return roomTo;
    }
}
