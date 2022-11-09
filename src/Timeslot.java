public class Timeslot {
    private final int periodNum;
    private final int weekNum;

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
