public class Exam {
    private int examID;
    private String examSub;
    private String requiredRoomType;
    private int[] students;
    private int[] classes;
    private Timeslot timeslot;
    private Room room;
    private boolean examSet;

    public Exam() {
        this.examSet = false;
    }

    public Exam(int examID) {
        this.examID = examID;
        this.examSet = false;
    }

    public Exam(int examID, String examSub, String requiredRoomType) {
        this.examID = examID;
        this.examSub = examSub;
        this.requiredRoomType = requiredRoomType;
        this.examSet = false;
    }

    public Exam(int examID, String examSub, String requiredRoomType, int[] classes, int[] students) {
        this.examID = examID;
        this.examSub = examSub;
        this.requiredRoomType = requiredRoomType;
        this.classes = classes;
        this.students = students;
        this.examSet = false;
    }

    public Exam(int examID, String examSub, String requiredRoomType, int[] classes, int[] students, int weekNum, int periodNum, Room room) {
        this.examID = examID;
        this.examSub = examSub;
        this.requiredRoomType = requiredRoomType;
        this.classes = classes;
        this.students = students;
        this.room = room;
        this.examSet = false;
        this.timeslot = new Timeslot(weekNum, periodNum);
    }

    public void setTimeslot(int weekNum, int periodNum) {
        this.timeslot = new Timeslot(weekNum, periodNum);
    }

    public void setTimeslot(Timeslot timeslot) {
        this.timeslot = timeslot;
    }

    public Timeslot getTimeslot() {
        return timeslot;
    }

    public int getExamID() {
        return examID;
    }

    public String getExamSub() {
        return examSub;
    }

    public String getRequiredRoomType() {
        return requiredRoomType;
    }

    public int[] getStudents() {
        return this.students;
    }

    public int enrolment() {
        return this.students.length;
    }


    public int getWeekNum() {
        return this.timeslot.getWeekNum();
    }

    public int getPeriodNum() {
        return this.timeslot.getPeriodNum();
    }

    public void setRequiredRoomType(String requiredRoomType) {
        this.requiredRoomType = requiredRoomType;
    }

    public void setStudents(int[] students) {
        this.students = students;
    }

    public void setExamSub(String examSub) {
        this.examSub = examSub;
    }

    public int[] getClasses() {
        return classes;
    }

    public void setClasses(int[] classes) {
        this.classes = classes;
    }

    public boolean isExamSet() {
        return examSet;
    }

    public void setExamSet(boolean examSet) {
        this.examSet = examSet;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}
