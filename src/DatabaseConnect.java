import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnect {
    private static Connection conn = null;

    public DatabaseConnect() {
        try {
            Class.forName("org.sqlite.JDBC");//Specify the SQLite Java driver
            conn = DriverManager.getConnection("jdbc:sqlite:TimetablingDB.db");//Specify the database, since relative in the main project folder
            conn.setAutoCommit(false);// Important as you want control of when data is written
            System.out.println("Opened database successfully");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    public void close() {
        try {
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Exam[] getAllExams() throws Exception {
        boolean bSelect = false;
        Statement stmt;
        ResultSet rs;
        LinkedList<Exam> tempExams = new LinkedList<>();
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM Exams;");

            while (rs.next()) {
                int examID = rs.getInt("ExamID");
                String examSubject = rs.getString("ExamSubject");
                String roomTypeRequired = rs.getString("RoomTypeRequired");
                int[] classes = getClassesOfExam(examID);
                LinkedList<Integer> tempStudents = new LinkedList<>();
                for (int aClass : classes) {
                    int[] classStudents = getStudentsOfClasses(aClass);
                    for (int classStudent : classStudents) {
                        tempStudents.append(classStudent);
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
        catch (SQLException e) {
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

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT StudentID FROM ClassEnrolment WHERE ClassID = " + classID + ";");
            while (rs.next()) {
                int studentID = rs.getInt("StudentID");
                tempStudents.append(studentID);
            }
            rs.close();
            stmt.close();
            bSelect = true;
        }
        catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        if (!bSelect)
            throw new Exception("Get students of classes query failed");
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

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT ClassID FROM ExamEnrolment WHERE ExamID = " + examID + ";");
            while (rs.next()) {
                int classID = rs.getInt("ClassID");
                tempClasses.append(classID);
            }
            rs.close();
            stmt.close();
            bSelect = true;
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        if (!bSelect)
            throw new Exception("Get classes of exam query failed");
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
        try {
            stmt1 = conn.createStatement();
            rs1 = stmt1.executeQuery("SELECT COUNT(WeekNumber) as numSlots FROM TimeSlots " +
                    "WHERE UsableForExams = 1 ORDER BY WeekNumber ASC, PeriodNumber ASC;");
            TRC = new ConflictNode[rs1.getInt("numSlots")][];
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT WeekNumber, PeriodNumber FROM TimeSlots " +
                    "WHERE UsableForExams = 1 ORDER BY WeekNumber ASC, PeriodNumber ASC;");
            int slotCounter = 0;
            while (rs.next()) {
                int weekNumber = rs.getInt("WeekNumber");
                int periodNumber = rs.getInt("PeriodNumber");
                LinkedList<ConflictNode> roomsInTimeslot = new LinkedList<>();
                for (Room room : rooms) {
                    boolean availability = getRoomAvailability(room, weekNumber, periodNumber);
                    roomsInTimeslot.append(new ConflictNode(weekNumber, periodNumber, room, availability));
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
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        if (!bSelect)
            throw new Exception("Get Timeslot Room Conflict Matrix query failed");
        return TRC;
    }

    private boolean getRoomAvailability(Room room, int weekNumber, int periodNumber) throws Exception {
        boolean bSelect = false;
        Statement stmt;
        ResultSet rs;
        boolean availability = true;

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT Availability FROM RoomAvailability WHERE RoomID = " + room.getRoomID()
                    + " AND WeekNumber = " + weekNumber + " AND PeriodNumber = " + periodNumber + ";");
            while (rs.next()) {
                availability = (rs.getInt("Availability") == 1);
            }
            rs.close();
            stmt.close();
            bSelect = true;
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        if (!bSelect)
            throw new Exception("Get room availability query failed");
        return availability;
    }

    private Room[] getAllRooms() throws Exception {
        boolean bSelect = false;
        Statement stmt;
        ResultSet rs;
        Room[] rooms;
        LinkedList<Room> tempRooms = new LinkedList<>();

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM Room;");
            while (rs.next()) {
                int roomID = rs.getInt("RoomID");
                int capacity = rs.getInt("Capacity");
                String roomType = rs.getString("Type");
                tempRooms.append(new Room(roomID, capacity, roomType));
            }
            rs.close();
            stmt.close();
            bSelect = true;
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        if (!bSelect)
            throw new Exception("Get all rooms query failed");
        rooms = new Room[tempRooms.len()];
        for (int i = 0; i < tempRooms.len(); i++) {
            rooms[i] = tempRooms.getValue(i);
        }
        return rooms;
    }

    public void addTimetable(Exam[][] timetable) throws Exception {
        for (Exam[] exams : timetable) {
            if (exams == null)
                continue;
            for (Exam exam : exams) {
                addExamToTimetable(exam);
            }
        }
    }

    private void addExamToTimetable(Exam exam) throws Exception {
        boolean bSelect = false;
        Statement stmt;
        try {
            stmt = conn.createStatement();
            String sql = "INSERT INTO Timetable (ExamID, PeriodNumber, WeekNumber, RoomID, InvigilatorID) VALUES ("
                    + exam.getExamID() + ", " + exam.getPeriodNum() + ", " + exam.getWeekNum() + ", "
                    + exam.getRoom().getRoomID() + ", " + exam.getInvigilator().getInvID() +");";
            stmt.executeUpdate(sql);
            stmt.close();
            conn.commit();
            bSelect = true;
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        if (!bSelect)
            throw new Exception("Unable to add exam " + exam.getExamID() + " to timetable table");
        System.out.println("added exams");
    }

    public Invigilator[] getAllInvigilators() throws Exception {
        boolean bSelect = false;
        Statement stmt;
        ResultSet rs;
        LinkedList<Invigilator> tempInvigilators = new LinkedList<>();
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM Invigilator;");

            while (rs.next()) {
                int invigilatorID = rs.getInt("InvigilatorID");
                int contractedExamsLeft = rs.getInt("ContractedExamsLeft");
                tempInvigilators.append(new Invigilator(invigilatorID, contractedExamsLeft));
            }
            rs.close();
            stmt.close();
            bSelect = true;
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        if (!bSelect)
            throw new Exception("Get all invigilators query failed");
        Invigilator[] invigilators = new Invigilator[tempInvigilators.len()];
        for (int i = 0; i < tempInvigilators.len(); i++) {
            invigilators[i] = tempInvigilators.getValue(i);
        }
        return invigilators;
    }
}