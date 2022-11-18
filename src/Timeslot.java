public record Timeslot(int weekNum, int periodNum) {

    public int getPeriodNum() {
        return periodNum;
    }

    public int getWeekNum() {
        return weekNum;
    }
}
