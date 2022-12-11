public class Room { //class to hold information about a room
    private final int RoomID;
    private final int capacity;
    private final String type;

    public Room(int RoomID, int capacity, String type) throws Exception {
        this.RoomID = RoomID;
        this.capacity = capacity; //computation for type makes it so Room can't be a record
        if (!type.equals("Computer") && !type.equals("Normal") && !type.equals("Sports")) {
            throw new Exception("Invalid room type provided");
        }
        this.type = type;
    }

    public int getRoomID() {
        return RoomID;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getType() {
        return type;
    }
}
