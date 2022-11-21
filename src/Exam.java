public class Exam {
    private final int examID;
    private final String examSub;
    private final String requiredRoomType;
    private final int[] students;
    private final int[] classes;
    private Timeslot timeslot;
    private Room room;
    private boolean examSet;
    private Invigilator invigilator;

    public Exam(int examID, String examSub, String requiredRoomType, int[] classes, int[] students) throws Exception {
        if (!requiredRoomType.equals("Computer") && !requiredRoomType.equals("Normal") && !requiredRoomType.equals("Sports")) {
            throw new Exception("Invalid room type provided");
        }
        this.examID = examID;
        this.examSub = examSub;
        this.requiredRoomType = requiredRoomType;
        this.classes = classes;
        this.students = students;
        this.examSet = false;
        this.invigilator = null;
    }

    public Exam(int examID, String examSub, String requiredRoomType, int[] classes, int[] students, int weekNum, int periodNum, Room room) throws Exception {
        if (!requiredRoomType.equals("Computer") && !requiredRoomType.equals("Normal") && !requiredRoomType.equals("Sports")) {
            throw new Exception("Invalid room type provided");
        }
        this.examID = examID;
        this.examSub = examSub;
        this.requiredRoomType = requiredRoomType;
        this.classes = classes;
        this.students = students;
        this.room = room;
        this.examSet = false;
        this.timeslot = new Timeslot(weekNum, periodNum);
        this.invigilator = null;
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

    public int[] getClasses() {
        return classes;
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

    public Invigilator getInvigilator() {
        return this.invigilator;
    }

    public void setInvigilator(Invigilator invigilator) {
        this.invigilator = invigilator;
    }
}
