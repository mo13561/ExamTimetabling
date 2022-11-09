public class Timeslot {
    private int periodNum;
    private int weekNum;

    public Timeslot(int weekNum, int periodNum) {
        this.weekNum = weekNum;
        this.periodNum = periodNum;
    }

    public int getPeriodNum() {
        return periodNum;
    }

    public int getWeekNum() {
        return weekNum;
    }
}
