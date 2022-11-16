public class Invigilator {
    private int invID; // unique invigilator ID
    private int examsLeft; // exams left to invigilate in their contract

    public Invigilator(int invID, int examsLeft) {
        this.invID = invID;
        this.examsLeft = examsLeft;
    }

    public int getInvID() {
        return invID;
    }

    public void setInvID(int invID) {
        this.invID = invID;
    }

    public int getExamsLeft() {
        return examsLeft;
    }

    public void setExamsLeft(int examsLeft) {
        this.examsLeft = examsLeft;
    }
}
