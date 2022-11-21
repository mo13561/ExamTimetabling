public final class Invigilator {
    private final int invID;
    private final int examsLeft;

    public Invigilator(int invID, int examsLeft) {
        this.invID = invID;
        this.examsLeft = examsLeft;
    }

    public int getInvID() {
        return invID;
    }

    public int getExamsLeft() {
        return examsLeft;
    }

    public int invID() {
        return invID;
    }

    public int examsLeft() {
        return examsLeft;
    }
}
