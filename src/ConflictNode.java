public class ConflictNode {
    private final Timeslot timeslot;
    private final Room room;
    private boolean available;

    public ConflictNode (int weekNum, int periodNum, Room room, boolean available) {
        this.available = available;
        this.timeslot = new Timeslot(weekNum, periodNum);
        this.room = room;
    }

    public Timeslot getTimeslot() {
        return timeslot;
    }

    public Room getRoom() {
        return room;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
