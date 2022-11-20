public record Student(int studentID, String studentName, int yearGroup) {

    public int getStudentID() {
        return studentID;
    }

    public String getStudentName() {
        return studentName;
    }

    public int getYearGroup() {
        return yearGroup;
    }
}
