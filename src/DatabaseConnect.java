import java.sql.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnect
{
    private static Connection conn = null;

    public static void main(String[] args) throws Exception {
        DatabaseConnect conn = new DatabaseConnect();
        //conn.createTable();
        //conn.insert();
        //conn.select();
        //conn.update();
        conn.close();
    }

    public DatabaseConnect()
    {
        try
        {
            Class.forName("org.sqlite.JDBC");//Specify the SQLite Java driver
            conn = DriverManager.getConnection("jdbc:sqlite:FINALDBMOVIERENT (1).db");//Specify the database, since relative in the main project folder
            conn.setAutoCommit(false);// Important as you want control of when data is written
            System.out.println("Opened database successfully");
        } catch (Exception e)
        {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    public void close()
    {
        try
        {
            conn.close();
        }
        catch (SQLException ex)
        {
            Logger.getLogger(DatabaseConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Exam[] getAllExams() throws Exception {
        boolean bSelect = false;
        Statement stmt;
        ResultSet rs;
        LinkedList<Exam> tempExams = new LinkedList<>();
        try
        {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM Exams;");

            while (rs.next())
            {
                int examID = rs.getInt("ExamID");
                String examSubject = rs.getString("ExamSubject");
                String roomTypeRequired = rs.getString("RoomTypeRequired");
                int[] classes = getClassesOfExam(examID);
                LinkedList<Integer> tempStudents = new LinkedList<>();
                for (int j = 0; j < classes.length; j++) {
                    int[] classStudents = getStudentsOfClasses(classes[j]);
                    for (int k = 0; k < classStudents.length; k++) {
                        tempStudents.append(classStudents[k]);
                    }
                }
                int[] students = new int[tempStudents.len()];
                for (int i = 0; i < tempStudents.len(); i++) {
                    students[i] = tempStudents.getValue(i);
                }
                tempExams.append(new Exam(examID, examSubject, roomTypeRequired, classes, students));
            }
            rs.close();
            stmt.close();
            bSelect = true;
        }
        catch (SQLException e)
        {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        if (!bSelect)
                throw new Exception("Get all exams query failed");
        Exam[] exams = new Exam[tempExams.len()];
        for (int i = 0; i < tempExams.len(); i++) {
            exams[i] = tempExams.getValue(i);
        }
        return exams;
    }

    private int[] getStudentsOfClasses(int classID) throws Exception {
        boolean bSelect = false;
        Statement stmt;
        ResultSet rs;
        LinkedList<Integer> tempStudents = new LinkedList<>();

        try
        {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT ClassID FROM ClassEnrolment WHERE ClassID = " + classID + ";");
            while (rs.next())
            {
                int studentID = rs.getInt("StudentID");
                tempStudents.append(studentID);
            }
            rs.close();
            stmt.close();
            bSelect = true;
        }
        catch (SQLException e)
        {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        int[] students = new int[tempStudents.len()];
        for (int i = 0; i < tempStudents.len(); i++) {
            students[i] = tempStudents.getValue(i);
        }
        return students;
    }

    private int[] getClassesOfExam(int examID) throws Exception {
        boolean bSelect = false;
        Statement stmt;
        ResultSet rs;
        LinkedList<Integer> tempClasses = new LinkedList<>();

        try
        {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT ClassID FROM ExamEnrolment WHERE ExamID = " + examID + ";");
            while (rs.next())
            {
                int classID = rs.getInt("ClassID");
                tempClasses.append(classID);
            }
            rs.close();
            stmt.close();
            bSelect = true;
        }
        catch (SQLException e)
        {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        int[] classes = new int[tempClasses.len()];
        for (int i = 0; i < tempClasses.len(); i++) {
            classes[i] = tempClasses.getValue(i);
        }
        return classes;
    }

    public ConflictNode[][] getTRC() throws Exception {
        boolean bSelect = false;
        Statement stmt;
        Statement stmt1;
        ResultSet rs;
        ResultSet rs1;
        Room[] rooms = getAllRooms();
        ConflictNode[][] TRC = new ConflictNode[0][];
        try
        {
            stmt1 = conn.createStatement();
            rs1 = stmt1.executeQuery("SELECT COUNT(WeekNumber, PeriodNumber) as numSlots FROM TimeSlots WHERE UsableForExams = 1 ORDER BY WeekNumber ASC, PeriodNumber ASC;");
            TRC = new ConflictNode[rs1.getInt("numSlots")][];
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT WeekNumber, PeriodNumber FROM TimeSlots WHERE UsableForExams = 1 ORDER BY WeekNumber ASC, PeriodNumber ASC;");
            int slotCounter = 0;
            while (rs.next())
            {
                int weekNumber = rs.getInt("WeekNumber");
                int periodNumber = rs.getInt("PeriodNumber");
                LinkedList<ConflictNode> roomsInTimeslot = new LinkedList<>();
                for (int i = 0; i < rooms.length; i++) {
                    boolean availability = getRoomAvailability(rooms[i], weekNumber, periodNumber);
                    roomsInTimeslot.append(new ConflictNode(weekNumber, periodNumber, rooms[i], availability));
                }
                TRC[slotCounter] = new ConflictNode[roomsInTimeslot.len()];
                for (int i = 0; i < roomsInTimeslot.len(); i++) {
                    TRC[slotCounter][i] = roomsInTimeslot.getValue(i);
                }
                slotCounter++;
            }
            rs.close();
            stmt.close();
            bSelect = true;
        }
        catch (SQLException e)
        {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return TRC;
    }

    private boolean getRoomAvailability(Room room, int weekNumber, int periodNumber) {
        boolean bSelect = false;
        Statement stmt;
        ResultSet rs;
        boolean availability = true;

        try
        {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT Availability FROM RoomAvailability WHERE RoomID = " + room.getRoomID() + " AND WeekNumber = " + weekNumber + " AND PeriodNumber = " + periodNumber + ";");
            while (rs.next())
            {
                availability = (rs.getInt("Availability") == 1);
            }
            rs.close();
            stmt.close();
            bSelect = true;
        }
        catch (SQLException e)
        {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return availability;
    }

    private Room[] getAllRooms() throws Exception {
        boolean bSelect = false;
        Statement stmt;
        ResultSet rs;
        Room[] rooms;
        LinkedList<Room> tempRooms = new LinkedList<>();

        try
        {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM Room;");
            while (rs.next())
            {
                int roomID = rs.getInt("RoomID");
                int capacity = rs.getInt("Capacity");
                String roomType = rs.getString("Type");
                tempRooms.append(new Room(roomID, capacity, roomType));
            }
            rs.close();
            stmt.close();
            bSelect = true;
        }
        catch (SQLException e)
        {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        rooms = new Room[tempRooms.len()];
        for (int i = 0; i < tempRooms.len(); i++) {
            rooms[i] = tempRooms.getValue(i);
        }
        return rooms;
    }
}