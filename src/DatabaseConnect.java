import java.sql.*;
import java.time.LocalDate;
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

    public int[] getStudentsOfClasses(int classID) throws Exception {
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

    public boolean studentInDatabase(int studentID) throws Exception {
        boolean bSelect = false;
        Statement stmt;
        ResultSet rs;
        int studentInDatabase = -1;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT COUNT(StudentID) as thing FROM Students WHERE StudentID = " + studentID + ";");
            studentInDatabase = rs.getInt("thing");
            rs.close();
            stmt.close();
            bSelect = true;
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        if (!bSelect)
            throw new Exception("Check if student in database query failed");
        return studentInDatabase == 1;
    }

    public void addStudent(int studentID, String studentName, int yearStartedY7) throws Exception {
        boolean bSelect = false;
        Statement stmt;
        try {
            stmt = conn.createStatement();
            String sql = "INSERT INTO Students (StudentID, StudentName, yearStartedY7) VALUES ("
                    + studentID + ", '" + studentName + "', " + yearStartedY7 + ");";
            stmt.executeUpdate(sql);
            stmt.close();
            conn.commit();
            bSelect = true;
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        if (!bSelect)
            throw new Exception("Unable to add student " + studentID + " to database");
        System.out.println("Added student " + studentName + ", ID: " + studentID);
    }

    public void removeStudent(int studentID) throws Exception {
        boolean bSelect = false;
        Statement stmt;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate("DELETE FROM Students WHERE StudentID = " + studentID +";");
            stmt.executeUpdate("DELETE FROM ClassEnrolment WHERE StudentID = " + studentID +";");
            stmt.close();
            conn.commit();
            bSelect = true;
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        if (!bSelect)
            throw new Exception("Unable to remove student " + studentID + " from all tables");
        System.out.println("Removed student, ID: " + studentID);
    }

    public Student getStudent(int studentID) throws Exception {
        boolean bSelect = false;
        Statement stmt;
        ResultSet rs;
        Student student = new Student(-1, "", -1);
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM Students WHERE StudentID = " + studentID + ";");
            student = new Student(rs.getInt("StudentID"), rs.getString("Name"), yearStartedToYearGroup(rs.getInt("yearStartedY7")));
            rs.close();
            stmt.close();
            bSelect = true;
        }
        catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        if (!bSelect)
            throw new Exception("Get student query failed");
        return student;
    }

    public int yearStartedToYearGroup(int yearStartedY7) {
        LocalDate date = LocalDate.now();
        if (date.getMonthValue() >= 9) {
            return 7 + (date.getYear() - yearStartedY7);
        }
        return 7 + (date.getYear() - yearStartedY7 - 1);
    }

    public void editStudentName(int studentID, String studentName) throws Exception {
        boolean bSelect = false;
        Statement stmt;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate("UPDATE Students SET Name = '" + studentName + "' WHERE StudentID = " + studentID +";");
            stmt.close();
            conn.commit();
            bSelect = true;
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        if (!bSelect)
            throw new Exception("Unable to edit student " + studentID + " in Students table");
        System.out.println("Edited student, ID: " + studentID + " -- new name: " + studentName);
    }

    public void editStudentYearStartedY7(int studentID, int yearStartedY7) throws Exception {
        boolean bSelect = false;
        Statement stmt;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate("UPDATE Students SET yearStartedY7 = " + yearStartedY7 + " WHERE StudentID = " + studentID +";");
            stmt.close();
            conn.commit();
            bSelect = true;
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        if (!bSelect)
            throw new Exception("Unable to edit student " + studentID + " in Students table");
        System.out.println("Edited student, ID: " + studentID + " -- new year group: " + yearStartedToYearGroup(yearStartedY7));
    }

    public boolean invigilatorInDatabase(int invigilatorID) throws Exception {
        boolean bSelect = false;
        Statement stmt;
        ResultSet rs;
        int invigilatorInDatabase = -1;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT COUNT(InvigilatorID) as thing FROM Invigilator WHERE InvigilatorID = " + invigilatorID + ";");
            invigilatorInDatabase = rs.getInt("thing");
            rs.close();
            stmt.close();
            bSelect = true;
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        if (!bSelect)
            throw new Exception("Check if invigilator in database query failed");
        return invigilatorInDatabase == 1;
    }

    public void addInvigilator(int invigilatorID, int examsLeft) throws Exception {
        boolean bSelect = false;
        Statement stmt;
        try {
            stmt = conn.createStatement();
            String sql = "INSERT INTO Invigilator (InvigilatorID, ContractedExamsLeft) VALUES ("
                    + invigilatorID + ", " + examsLeft + ");";
            stmt.executeUpdate(sql);
            stmt.close();
            conn.commit();
            bSelect = true;
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        if (!bSelect)
            throw new Exception("Unable to add invigilator " + invigilatorID + " to database");
        System.out.println("Added invigilator, ID: " + invigilatorID + ", contracted exams left: " + examsLeft);
    }

    public Invigilator getInvigilator(int invigilatorID) throws Exception {
        boolean bSelect = false;
        Statement stmt;
        ResultSet rs;
        Invigilator invigilator = new Invigilator(invigilatorID, -1);
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM Invigilator WHERE InvigilatorID = " + invigilatorID + ";");
            invigilator = new Invigilator(invigilatorID, rs.getInt("ContractedExamsLeft"));
            rs.close();
            stmt.close();
            bSelect = true;
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        if (!bSelect)
            throw new Exception("Get invigilator, ID: " + invigilatorID + " query failed");
        return invigilator;
    }

    public void editInvigilatorExamsLeft(int invigilatorID, int newExamsLeft) throws Exception {
        boolean bSelect = false;
        Statement stmt;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate("UPDATE Invigilator SET ContractedExamsLeft = " + newExamsLeft + " WHERE InvigilatorID = " + invigilatorID +";");
            stmt.close();
            conn.commit();
            bSelect = true;
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        if (!bSelect)
            throw new Exception("Unable to edit invigilator, ID: " + invigilatorID + " in Invigilator table");
        System.out.println("Edited invigilator, ID: " + invigilatorID + " -- new contracted exams left: " + newExamsLeft);
    }

    public void removeInvigilator(int invigilatorID) throws Exception {
        boolean bSelect = false;
        Statement stmt;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate("DELETE FROM Invigilator WHERE InvigilatorID = " + invigilatorID +";");
            stmt.executeUpdate("DELETE FROM Timetable WHERE InvigilatorID = " + invigilatorID +";");
            stmt.close();
            conn.commit();
            bSelect = true;
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        if (!bSelect)
            throw new Exception("Unable to remove invigilator, ID: " + invigilatorID + " from all tables");
        System.out.println("Removed invigilator, ID: " + invigilatorID);
    }

    public void enrolStudentInClass(int classID, int studentID) throws Exception {
        boolean bSelect = false;
        Statement stmt;
        try {
            stmt = conn.createStatement();
            String sql = "INSERT INTO ClassEnrolment (StudentID, ClassID) VALUES ("
                    + studentID + ", " + classID + ");";
            stmt.executeUpdate(sql);
            stmt.close();
            conn.commit();
            bSelect = true;
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        if (!bSelect)
            throw new Exception("Unable to enrol student, ID : " + studentID + " to class, ID: " + classID);
        System.out.println("Enrolled student, ID : " + studentID + " to class, ID: " + classID);
    }

    public void removeClass(int classID) throws Exception {
        boolean bSelect = false;
        Statement stmt;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate("DELETE FROM Classes WHERE ClassID = " + classID +";");
            stmt.executeUpdate("DELETE FROM ClassEnrolment WHERE ClassID = " + classID +";");
            stmt.executeUpdate("DELETE FROM ExamEnrolment WHERE ClassID = " + classID +";");
            stmt.close();
            conn.commit();
            bSelect = true;
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        if (!bSelect)
            throw new Exception("Unable to remove class, ID: " + classID + " from all tables");
        System.out.println("Removed class, ID: " + classID);
    }

    public boolean classInDatabase(int classID) throws Exception {
        boolean bSelect = false;
        Statement stmt;
        ResultSet rs;
        int classInDatabase = -1;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT COUNT(ClassID) as thing FROM Classes WHERE ClassID = " + classID + ";");
            classInDatabase = rs.getInt("thing");
            rs.close();
            stmt.close();
            bSelect = true;
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        if (!bSelect)
            throw new Exception("Check if class, ID: " + classID + " in database query failed");
        return classInDatabase == 1;
    }

    public void addClass(int classID, String type, int yearGroup) throws Exception {
        boolean bSelect = false;
        Statement stmt;
        try {
            stmt = conn.createStatement();
            String sql = "INSERT INTO Classes (ClassID, ClassType, YearGroup) VALUES ("
                    + classID + ", '" + type + "', " + yearGroup + ");";
            stmt.executeUpdate(sql);
            stmt.close();
            conn.commit();
            bSelect = true;
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        if (!bSelect)
            throw new Exception("Unable to add class " + classID + " to database");
        System.out.println("Added class, ID: " + classID + ", subject type: " + type);
    }

    public SClass getSClass(int classID) throws Exception {
        boolean bSelect = false;
        Statement stmt;
        ResultSet rs;
        SClass sClass = new SClass(-1, "", -1);
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM Classes WHERE ClassID = " + classID + ";");
            sClass = new SClass(rs.getInt("ClassID"), rs.getString("ClassType"), rs.getInt("YearGroup"));
            rs.close();
            stmt.close();
            bSelect = true;
        }
        catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        if (!bSelect)
            throw new Exception("Get class query failed");
        return sClass;
    }

    public void editClassType(int classID, String classType) throws Exception {
        boolean bSelect = false;
        Statement stmt;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate("UPDATE Classes SET ClassType = " + classType + " WHERE ClassID = " + classID +";");
            stmt.close();
            conn.commit();
            bSelect = true;
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        if (!bSelect)
            throw new Exception("Unable to edit class, ID: " + classID + " in Classes table");
        System.out.println("Edited class, ID: " + classID + " -- new class subject type: " + classType);
    }

    public void editClassYearGroup(int classID, int yearGroup) throws Exception {
        boolean bSelect = false;
        Statement stmt;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate("UPDATE Classes SET YearGroup = " + yearGroup + " WHERE ClassID = " + classID +";");
            stmt.close();
            conn.commit();
            bSelect = true;
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        if (!bSelect)
            throw new Exception("Unable to edit class, ID: " + classID + " in Classes table");
        System.out.println("Edited class, ID: " + classID + " -- new class year group: " + yearGroup);
    }

    public void removeStudentFromClass(int studentID, int classID) throws Exception {
        boolean bSelect = false;
        Statement stmt;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate("DELETE FROM ClassEnrolment WHERE StudentID = " + studentID +" AND ClassID = " + classID + ";");
            stmt.close();
            conn.commit();
            bSelect = true;
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        if (!bSelect)
            throw new Exception("Unable to remove student " + studentID + " from class, ID: " + classID);
        System.out.println("Removed student, ID: " + studentID + " from class, ID: " + classID);
    }
}