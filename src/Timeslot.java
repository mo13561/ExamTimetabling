public record Timeslot(int weekNum, int periodNum) { //record to store a timeslot

    public int getPeriodNum() {
        return periodNum;
    }

    public int getWeekNum() {
        return weekNum;
    }
}
