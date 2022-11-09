public class Room {
    private int RoomID;
    private int capacity;
    private String type;

    public Room(int RoomID, int capacity, String type) throws Exception {
        this.RoomID = RoomID;
        this.capacity = capacity;
        if (type != "Computer" || type != "Normal" || type != "Sports") {
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
