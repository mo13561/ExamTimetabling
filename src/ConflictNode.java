public class ConflictNode {
    private Timeslot timeslot;
    private Room room;
    private boolean available;

    public ConflictNode (int weekNum, int periodNum, int roomID, int capacity, String roomType, boolean available) throws Exception {
        this.available = available;
        this.timeslot = new Timeslot(weekNum, periodNum);
        this.room = new Room(roomID, capacity, roomType);
    }

    public ConflictNode (int weekNum, int periodNum, Room room, boolean available) throws Exception {
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
