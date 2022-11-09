public class Exam {
    private int examID;
    private String examSub;
    private String requiredRoomType;
    private int[] students;
    private int[] classes;
    private int weekNum;
    private int periodNum;
    private int roomID;

    public Exam() {}

    public Exam(int examID) {
        this.examID = examID;
    }

    public Exam(int examID, String examSub, String requiredRoomType) {
        this.examID = examID;
        this.examSub = examSub;
        this.requiredRoomType = requiredRoomType;
    }

    public Exam(int examID, String examSub, String requiredRoomType, int[] classes, int[] students) {
        this.examID = examID;
        this.examSub = examSub;
        this.requiredRoomType = requiredRoomType;
        this.classes = classes;
        this.students = students;
    }

    public Exam(int examID, String examSub, String requiredRoomType, int[] classes, int[] students, int weekNum, int periodNum, int roomID) {
        this.examID = examID;
        this.examSub = examSub;
        this.requiredRoomType = requiredRoomType;
        this.classes = classes;
        this.students = students;
        this.weekNum = weekNum;
        this.periodNum = periodNum;
        this.roomID = roomID;
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
        return students;
    }

    public int enrolment() {
        return students.length;
    }

    public void setTimeSlot(int weekNum, int periodNum) {
        this.weekNum = weekNum;
        this.periodNum = periodNum;
    }

    public int[] getTimeSlot() {
        return new int[]{this.weekNum, this.periodNum};
    }

    public int getWeekNum() {
        return weekNum;
    }

    public void setWeekNum(int weekNum) {
        this.weekNum = weekNum;
    }

    public int getPeriodNum() {
        return periodNum;
    }

    public void setPeriodNum(int periodNum) {
        this.periodNum = periodNum;
    }

    public int getRoomID() {
        return roomID;
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
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
}
