import java.time.LocalDate;
import java.util.Scanner;

public class Menu {
    final Scanner sc = new Scanner(System.in);

    public void mainLoop() throws Exception {
        int response;
        do {
            System.out.println("1 -> Student Management");
            System.out.println("2 -> Invigilator Management");
            System.out.println("3 -> Timetable Production"); //TODO
            System.out.println("4 -> Class Management");
            System.out.println("5 -> Timeslot Management");
            System.out.println("6 -> Room Management");
            System.out.println("7 -> Quit");
            response = sc.nextInt();
            switch (response) {
                case 1 -> studentManagement();
                case 2 -> invigilatorManagement();
                case 3 -> timetableProduction();
                case 4 -> classManagement();
                case 5 -> timeslotManagement();
                case 6 -> roomManagement();
                case 7 -> System.out.println("You have now quit the application");
                default -> System.out.println("Invalid input, try again.");
            }
        } while (response != 7);
    }

    public static void main(String[] args) {
        try {
            Menu menu = new Menu();
            menu.mainLoop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void timetableProduction() throws Exception {
        int response;
        do {
            System.out.println("1 -> Exam Management");
            System.out.println("2 -> Construct Timetable");
            System.out.println("3 -> Display Current Timetable");
            System.out.println("4 -> Return");
            response = sc.nextInt();
            switch (response) {
                case 1 -> examManagement();
                case 2 -> constructTimetable(); //TODO
                case 3 -> displayTimetable(); //TODO
                case 4 -> System.out.println("Returning to previous section");
                default -> System.out.println("Invalid input, try again.");
            }
        } while (response != 4);
    }

    private void displayTimetable() {
    }

    private void constructTimetable() {
    }

    private void examManagement() throws Exception {
        int response;
        do {
            System.out.println("1 -> Add Exam");
            System.out.println("2 -> Remove Exam");
            System.out.println("3 -> Edit Exam");
            System.out.println("4 -> Get All Exams");
            System.out.println("5 -> Return");
            response = sc.nextInt();
            switch (response) {
                case 1 -> addExam();
                case 2 -> removeExam();
                case 3 -> editExam(); //TODO
                case 4 -> getAllExams(); //TODO
                case 5 -> System.out.println("Returning to previous section");
                default -> System.out.println("Invalid input, try again.");
            }
        } while (response != 5);
    }

    private void getAllExams() {
    }

    private void editExam() throws Exception {
        int response;
        do {
            System.out.println("1 -> Get All Exams");
            System.out.println("2 -> Get Exam Enrolment");
            System.out.println("3 -> Exam Enrolment Management");
            System.out.println("4 -> Return");
            response = sc.nextInt();
            switch (response) {
                case 1 -> getAllExams(); //TODO
                case 2 -> getExamEnrolment(); //TODO
                case 3 -> examEnrolmentManagement(); //TODO
                case 4 -> System.out.println("Returning to previous section");
                default -> System.out.println("Invalid input, try again.");
            }
        } while (response != 4);
    }

    private void examEnrolmentManagement() {
    }

    private void getExamEnrolment() {
    }

    private void removeExam() throws Exception {
        System.out.println("Enter unique Exam ID [number] -> ");
        int examID = sc.nextInt();
        DatabaseConnect conn = new DatabaseConnect();
        while (!conn.examInDatabase(examID)) {
            System.out.println("This exam is not present in the database");
            System.out.println("Enter unique Exam ID [number] -> ");
            examID = sc.nextInt();
        }
        sc.nextLine();
        conn.removeExam(examID);
        conn.close();
    }

    private void addExam() throws Exception {
        System.out.println("Enter unique Exam ID [number] -> ");
        int examID = sc.nextInt();
        DatabaseConnect conn = new DatabaseConnect();
        while (conn.examInDatabase(examID)) {
            System.out.println("This exam is already present in the database");
            System.out.println("Enter unique Exam ID [number] -> ");
            examID = sc.nextInt();
        }
        sc.nextLine();
        System.out.println("Enter exam subject type -> ");
        String examSub = sc.nextLine();
        System.out.println("Enter exam room type required -> ");
        String roomType = sc.nextLine();
        while (!roomType.equals("Computer") && !roomType.equals("Normal") && !roomType.equals("Sports")) {
            System.out.println("Invalid room type provided");
            System.out.println("Enter exam room type required -> ");
            roomType = sc.nextLine();
        }
        conn.addExam(examID, examSub, roomType);
        conn.close();
        int response;
        do {
            System.out.println("1 -> Enrol Class");
            System.out.println("2 -> Return");
            response = sc.nextInt();
            switch (response) {
                case 1 -> enrolClass(examID);
                case 2 -> System.out.println("Returning to previous section");
                default -> System.out.println("Invalid input, try again.");
            }
        } while (response != 2);
    }

    private void enrolClass(int examID) throws Exception {
        System.out.println("Enter unique Class ID -> ");
        int classID = sc.nextInt();
        DatabaseConnect conn = new DatabaseConnect();
        try {
            while (!conn.classInDatabase(classID)) {
                System.out.println("The class provided is not present in the database");
                System.out.println("Enter class ID -> ");
                classID = sc.nextInt();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (conn.classEnrolled(classID, examID)) {
            System.out.println("This class [ID: " + classID + "] is already enrolled in the exam [ID: " + examID + "]");
        } else {
            conn.enrolClassInExam(classID, examID);
        }
        conn.close();
    }

    private void roomManagement() throws Exception {
        int response;
        do {
            System.out.println("1 -> Add Room [initialise all usable timeslots available]");
            System.out.println("2 -> Remove Room");
            System.out.println("3 -> Edit Room Availability");
            System.out.println("4 -> Return");
            response = sc.nextInt();
            switch (response) {
                case 1 -> addRoom();
                case 2 -> removeRoom();
                case 3 -> editRoomAvailability();
                case 4 -> System.out.println("Returning to previous section");
                default -> System.out.println("Invalid input, try again.");
            }
        } while (response != 4);
    }

    private void editRoomAvailability() throws Exception {
        int response;
        do {
            System.out.println("1 -> Get available timeslots");
            System.out.println("2 -> Make timeslot available for room");
            System.out.println("3 -> Make timeslot unavailable for room");
            System.out.println("4 -> Get room availability");
            System.out.println("5 -> Return");
            response = sc.nextInt();
            switch (response) {
                case 1 -> getAvailableTimeslots();
                case 2 -> setRoomAvailability(true);
                case 3 -> setRoomAvailability(false);
                case 4 -> getRoomAvailability();
                case 5 -> System.out.println("Returning to previous section");
                default -> System.out.println("Invalid input, try again.");
            }
        } while (response != 5);
    }

    private void getRoomAvailability() {
        System.out.println("Enter unique Room ID [number] -> ");
        int roomID = sc.nextInt();
        ConflictNode[] roomAvailability = new ConflictNode[0];
        DatabaseConnect conn = new DatabaseConnect();
        try {
            while (!conn.roomInDatabase(roomID)) {
                System.out.println("The RoomID provided is not in the database");
                System.out.println("Enter unique Room ID [number] -> ");
                roomID = sc.nextInt();
            }
            roomAvailability = conn.getRoomAvailability(roomID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (ConflictNode conflictNode : roomAvailability) {
            System.out.println("[ " +
                    "WeekNumber = " + conflictNode.getTimeslot().getWeekNum() + " ," +
                    "PeriodNumber = " + conflictNode.getTimeslot().getPeriodNum() + " ," +
                    "Available ? " + conflictNode.isAvailable() + " ]");
        }
        System.out.println("Returning to previous section");
    }

    private void setRoomAvailability(boolean available) throws Exception {
        System.out.println("Enter unique Room ID [number] -> ");
        int roomID = sc.nextInt();
        DatabaseConnect conn = new DatabaseConnect();
        while (!conn.roomInDatabase(roomID)) {
            System.out.println("The RoomID provided is not in the database");
            System.out.println("Enter unique Room ID [number] -> ");
            roomID = sc.nextInt();
        }
        int[] timeslot = getTimeSlotInput();
        conn.setRoomAvailability(roomID, timeslot[0], timeslot[1], available);
        conn.close();
    }

    private void removeRoom() throws Exception {
        System.out.println("Enter unique Room ID [number] ->");
        int roomID = sc.nextInt();
        DatabaseConnect conn = new DatabaseConnect();
        if (conn.roomInDatabase(roomID)) {
            conn.removeRoom(roomID);
        } else {
            System.out.println("The room provided has not been added to the database");
        }
        System.out.println("Returning to previous section");
    }

    private void addRoom() throws Exception {
        System.out.println("Enter unique Room ID [number] -> ");
        int roomID = sc.nextInt();
        DatabaseConnect conn = new DatabaseConnect();
        while (conn.roomInDatabase(roomID)) {
            System.out.println("The RoomID provided is already in the database");
            System.out.println("Enter unique Room ID [number] -> ");
            roomID = sc.nextInt();
        }
        System.out.println("Enter room capacity ->");
        int capacity = sc.nextInt();
        sc.nextLine();
        while (capacity <= 0) {
            System.out.println("Invalid capacity entered");
            System.out.println("Enter room capacity ->");
            capacity = sc.nextInt();
        }
        System.out.println("Enter room type [Normal, Computer, Sports] -> ");
        String type = sc.nextLine();
        while (!type.equals("Computer") && !type.equals("Normal") && !type.equals("Sports")) {
            System.out.println("Invalid room type provided");
            System.out.println("Enter room type [Normal, Computer, Sports] -> ");
            type = sc.nextLine();
        }
        conn.addRoom(roomID, capacity, type);
        conn.close();
    }

    private void timeslotManagement() throws Exception {
        int response;
        do {
            System.out.println("1 -> Add Available Timeslot");
            System.out.println("2 -> Make Timeslot Unavailable");
            System.out.println("3 -> Get Available Timeslots");
            System.out.println("4 -> Return");
            response = sc.nextInt();
            switch (response) {
                case 1 -> addAvailableTimeslot();
                case 2 -> makeTimeslotUnavailable();
                case 3 -> getAvailableTimeslots();
                case 4 -> System.out.println("Returning to previous section");
                default -> System.out.println("Invalid input, try again.");
            }
        } while (response != 4);
    }

    private void getAvailableTimeslots() throws Exception {
        DatabaseConnect conn = new DatabaseConnect();
        Timeslot[] timeslots;
        timeslots = conn.getAvailableTimeslots();
        conn.close();
        for (int i = 0; i < timeslots.length; i++) {
            System.out.println("Timeslot " + i + " : " +  timeslots[i]);
        }
    }

    private int[] getTimeSlotInput() {
        System.out.println("Enter Week Number ->");
        int weekNumber = sc.nextInt();
        while (weekNumber < 1 || weekNumber > 39) {
            System.out.println("Invalid week number entered");
            System.out.println("Enter Week Number ->");
            weekNumber = sc.nextInt();
        }
        System.out.println("Enter Period Number ->");
        int periodNumber = sc.nextInt();
        while (periodNumber < 1 || periodNumber > 35) {
            System.out.println("Invalid period number entered");
            System.out.println("Enter Period Number ->");
            periodNumber = sc.nextInt();
        }
        return new int[]{weekNumber, periodNumber};
    }

    private void makeTimeslotUnavailable() throws Exception {
        int[] timeslot = getTimeSlotInput();
        DatabaseConnect conn = new DatabaseConnect();
        if (conn.timeslotInDatabase(timeslot[0], timeslot[1])) {
            conn.setTimeSlot(timeslot[0], timeslot[1], false);
        } else {
            conn.addTimeslot(timeslot[0], timeslot[1], false);
        }
        conn.close();
    }

    private void addAvailableTimeslot() throws Exception {
        int[] timeslot = getTimeSlotInput();
        int weekNumber = timeslot[0]; int periodNumber = timeslot[1];
        DatabaseConnect conn = new DatabaseConnect();
        if (conn.timeslotInDatabase(weekNumber, periodNumber)) {
            conn.setTimeSlot(weekNumber, periodNumber, true);
        } else {
            conn.addTimeslot(weekNumber, periodNumber, true);
        }
        conn.close();
    }

    private void classManagement() throws Exception {
        int response;
        do {
            System.out.println("1 -> Add Class");
            System.out.println("2 -> Edit Class");
            System.out.println("3 -> Remove Class");
            System.out.println("4 -> Get Class Information");
            System.out.println("5 -> Return");
            response = sc.nextInt();
            switch (response) {
                case 1 -> addClass();
                case 2 -> editClass();
                case 3 -> removeClass();
                case 4 -> getClassInformation();
                case 5 -> System.out.println("Returning to previous section");
                default -> System.out.println("Invalid input, try again.");
            }
        } while (response != 5);
    }

    private void getClassInformation() throws Exception {
        System.out.println("Enter unique Class ID [number]");
        int classID = sc.nextInt();
        DatabaseConnect conn = new DatabaseConnect();
        if (conn.classInDatabase(classID)) {
            SClass sClass = conn.getSClass(classID);
            System.out.println("Class ID : " + sClass.getClassID());
            System.out.println("Class subject type : " + sClass.getClassType());
            System.out.println("Class year group : " + sClass.getYearGroup());
            System.out.println("Students enrolled (IDs) : " + printStudents(conn.getStudentsOfClasses(classID)));
        } else {
            System.out.println("The class provided has not been added to the database");
        }
        conn.close();
        System.out.println("Returning to previous section");
    }

    private void removeClass() throws Exception {
        System.out.println("Enter unique Class ID [number]");
        int classID = sc.nextInt();
        DatabaseConnect conn = new DatabaseConnect();
        if (conn.classInDatabase(classID)) {
            conn.removeClass(classID);
        } else {
            System.out.println("The class provided has not been added to the database");
        }
        conn.close();
        System.out.println("Returning to previous section");
    }

    private void editClass() throws Exception {
        boolean classExists;
        System.out.println("Enter class ID of class to be edited -> ");
        int classID = sc.nextInt();
        DatabaseConnect conn = new DatabaseConnect();
        classExists = conn.classInDatabase(classID);
        if (classExists) {
            System.out.println("CURRENT INFORMATION:");
            SClass sClass = conn.getSClass(classID);
            System.out.println("Class ID : " + sClass.getClassID());
            System.out.println("Class subject type : " + sClass.getClassType());
            System.out.println("Class year group : " + sClass.getYearGroup());
            System.out.println("Students enrolled (IDs) : " + printStudents(conn.getStudentsOfClasses(classID)));
            conn.close();
            int response;
            do {
                System.out.println("1 -> Edit class subject type");
                System.out.println("2 -> Edit class enrolment");
                System.out.println("3 -> Edit class year group");
                System.out.println("4 -> Return");
                response = sc.nextInt();
                switch (response) {
                    case 1 -> editClassType(classID);
                    case 2 -> editClassEnrolment(classID);
                    case 3 -> editClassYearGroup(classID);
                    case 4 -> System.out.println("Returning to previous section");
                    default -> System.out.println("Invalid input, try again.");
                }
            } while (response != 4);
        }
    }

    private void editClassYearGroup(int classID) throws Exception {
        System.out.println("Enter new class year group -> ");
        int yearGroup = sc.nextInt();
        while (yearGroup < 7 || yearGroup > 11) {
            System.out.println("Invalid year group entered");
            System.out.println("Enter new class year group -> ");
            yearGroup = sc.nextInt();
        }
        DatabaseConnect conn = new DatabaseConnect();
        conn.editClassYearGroup(classID, yearGroup);
        conn.close();
    }

    private void editClassEnrolment(int classID) throws Exception {
        int response;
        do {
            System.out.println("1 -> Enrol student");
            System.out.println("2 -> Remove student");
            System.out.println("3 -> Return");
            response = sc.nextInt();
            switch (response) {
                case 1 -> enrolStudent(classID);
                case 2 -> removeStudentFromClass(classID);
                case 3 -> System.out.println("Returning to previous section");
                default -> System.out.println("Invalid input, try again.");
            }
        } while (response != 3);
    }

    private void removeStudentFromClass(int classID) throws Exception {
        System.out.println("Enter unique school Student ID [number] ->");
        int studentID = sc.nextInt();
        DatabaseConnect conn = new DatabaseConnect();
        if (conn.studentInDatabase(studentID)) {
            conn.removeStudentFromClass(studentID, classID);
        } else {
            System.out.println("The student provided has not been added to the database");
        }
        conn.close();
        System.out.println("Returning to previous section");
    }

    private void editClassType(int classID) throws Exception {
        System.out.println("Enter new class type -> ");
        String classType = sc.nextLine().trim();
        DatabaseConnect conn = new DatabaseConnect();
        conn.editClassType(classID, classType);
        conn.close();
    }

    private String printStudents(int[] students) {
        if (students == null || students.length == 0) {
            return "";
        }
        StringBuilder output = new StringBuilder("[ " + students[0]);
        for (int i = 1; i < students.length; i++) {
            output.append(", ").append(students[i]);
        }
        return output + " ]";
    }

    private void addClass() throws Exception {
        System.out.println("Enter class ID -> ");
        int classID = sc.nextInt();
        DatabaseConnect conn = new DatabaseConnect();
        while (conn.classInDatabase(classID)) {
            System.out.println("This class is already present in the database");
            System.out.println("Enter class ID -> ");
            classID = sc.nextInt();
        }
        sc.nextLine();
        System.out.println("Enter class subject type -> ");
        String type = sc.nextLine().trim();
        System.out.println("Enter class year group -> ");
        int yearGroup = sc.nextInt();
        while (yearGroup < 7 || yearGroup > 11) {
            System.out.println("Invalid year group entered");
            System.out.println("Enter class year group -> ");
            yearGroup = sc.nextInt();
        }
        conn.addClass(classID, type, yearGroup);
        conn.close();
        enrolStudents(classID);
        System.out.println("Returning to previous section");
    }

    private void enrolStudents(int classID) throws Exception {
        int response;
        do {
            System.out.println("1 -> Enrol student in class, ID: " + classID);
            System.out.println("2 -> Return");
            response = sc.nextInt();
            switch (response) {
                case 1 -> enrolStudent(classID);
                case 2 -> System.out.println("Returning to previous section");
                default -> System.out.println("Invalid input, try again.");
            }
        } while (response != 2);
    }

    private void enrolStudent(int classID) throws Exception {
        System.out.println("Enter student ID -> ");
        int studentID = sc.nextInt();
        DatabaseConnect conn = new DatabaseConnect();
        SClass sClass = conn.getSClass(classID);
        if (conn.studentInDatabase(studentID)) {
            Student student = conn.getStudent(studentID);
            if (student.getYearGroup() == sClass.getYearGroup()) {
                conn.enrolStudentInClass(classID, studentID);
            } else {
                System.out.println("The provided student was not in the same year group as the class, try again");
            }
        } else {
            System.out.println("The student provided has not been added to the database, try again");
        }
        conn.close();
    }

    private void invigilatorManagement() throws Exception {
        int response;
        do {
            System.out.println("1 -> Add Invigilator");
            System.out.println("2 -> Edit Invigilator");
            System.out.println("3 -> Remove Invigilator");
            System.out.println("4 -> Get Invigilator Information");
            System.out.println("5 -> Return");
            response = sc.nextInt();
            switch (response) {
                case 1 -> addInvigilator();
                case 2 -> editInvigilator();
                case 3 -> removeInvigilator();
                case 4 -> getInvigilatorInformation();
                case 5 -> System.out.println("Returning to previous section");
                default -> System.out.println("Invalid input, try again.");
            }
        } while (response != 5);
    }

    private void getInvigilatorInformation() throws Exception {
        System.out.println("Enter unique Invigilator ID [number]");
        int invigilatorID = sc.nextInt();
        DatabaseConnect conn = new DatabaseConnect();
        if (conn.invigilatorInDatabase(invigilatorID)) {
            Invigilator invigilator = conn.getInvigilator(invigilatorID);
            conn.close();
            System.out.println("Invigilator ID : " + invigilator.getInvID());
            System.out.println("Invigilator contracted exams left : " + invigilator.getExamsLeft());
        } else {
            System.out.println("The invigilator provided has not been added to the database");
        }
        System.out.println("Returning to previous section");
    }

    private void removeInvigilator() throws Exception {
        System.out.println("Enter unique Invigilator ID [number]");
        int invigilatorID = sc.nextInt();
        DatabaseConnect conn = new DatabaseConnect();
        if (conn.invigilatorInDatabase(invigilatorID)) {
            conn.removeInvigilator(invigilatorID);
        } else {
            System.out.println("The invigilator provided has not been added to the database");
        }
        conn.close();
        System.out.println("Returning to previous section");
    }

    private void editInvigilator() throws Exception {
        System.out.println("Enter unique Invigilator ID [number]");
        int invigilatorID = sc.nextInt();
        DatabaseConnect conn = new DatabaseConnect();
        if (!conn.invigilatorInDatabase(invigilatorID)) {
            System.out.println("The invigilator does not exist in the database");
        } else {
            System.out.println("CURRENT INFORMATION:");
            Invigilator invigilator = conn.getInvigilator(invigilatorID);
            System.out.println("Invigilator ID : " + invigilator.getInvID());
            System.out.println("Invigilator contracted exams left : " + invigilator.getExamsLeft());
            conn.close();
            int response;
            do {
                System.out.println("1 -> Edit Contracted Exams Left");
                System.out.println("2 -> Return");
                response = sc.nextInt();
                switch (response) {
                    case 1 -> editInvigilatorExamsLeft(invigilatorID);
                    case 2 -> System.out.println("Returning to previous section");
                    default -> System.out.println("Invalid input, try again.");
                }
            } while (response != 2);
        }
        System.out.println("Returning to previous section");
    }

    private void editInvigilatorExamsLeft(int invigilatorID) throws Exception {
        System.out.println("Enter new contracted exams left -> ");
        int newExamsLeft = sc.nextInt();
        DatabaseConnect conn = new DatabaseConnect();
        conn.editInvigilatorExamsLeft(invigilatorID, newExamsLeft);
        conn.close();
    }

    private void addInvigilator() throws Exception {
        System.out.println("Enter unique Invigilator ID [number]");
        int invigilatorID = sc.nextInt();
        System.out.println("Enter contracted exams left [number]");
        int examsLeft = sc.nextInt();
        DatabaseConnect conn = new DatabaseConnect();
        if (!conn.invigilatorInDatabase(invigilatorID)) {
            conn.addInvigilator(invigilatorID, examsLeft);
        } else {
            System.out.println("This invigilator is already in the database");
        }
        conn.close();
        System.out.println("Returning to previous section");
    }

    private void studentManagement() throws Exception {
        int response;
        do {
            System.out.println("1 -> Add Student");
            System.out.println("2 -> Edit Student");
            System.out.println("3 -> Remove Student");
            System.out.println("4 -> Get Student Information");
            System.out.println("5 -> Enrol student in class");
            System.out.println("6 -> Remove student from class");
            System.out.println("7 -> Return");
            response = sc.nextInt();
            switch (response) {
                case 1 -> addStudent();
                case 2 -> editStudent();
                case 3 -> removeStudent();
                case 4 -> getStudentInformation();
                case 5 -> enrolStudent();
                case 6 -> removeStudentFromClass();
                case 7 -> System.out.println("Returning to previous section");
                default -> System.out.println("Invalid input, try again.");
            }
        } while (response != 7);
    }

    private void removeStudentFromClass() throws Exception {
        System.out.println("Enter class ID -> ");
        int classID = sc.nextInt();
        DatabaseConnect conn = new DatabaseConnect();
        while (!conn.classInDatabase(classID)) {
            System.out.println("This class is not present in the database");
            System.out.println("Enter class ID -> ");
            classID = sc.nextInt();
        }
        conn.close();
        sc.nextLine();
        removeStudentFromClass(classID);
    }

    private void enrolStudent() throws Exception {
        System.out.println("Enter class ID -> ");
        int classID = sc.nextInt();
        DatabaseConnect conn = new DatabaseConnect();
        while (!conn.classInDatabase(classID)) {
            System.out.println("This class is not present in the database");
            System.out.println("Enter class ID -> ");
            classID = sc.nextInt();
        }
        conn.close();
        sc.nextLine();
        enrolStudent(classID);
    }

    private void getStudentInformation() throws Exception {
        System.out.println("Enter unique school Student ID [number]");
        int studentID = sc.nextInt();
        DatabaseConnect conn = new DatabaseConnect();
        if (conn.studentInDatabase(studentID)) {
            Student student = conn.getStudent(studentID);
            conn.close();
            System.out.println("Student ID : " + student.getStudentID());
            System.out.println("Student Name : " + student.getStudentName());
            System.out.println("Student year group : " + student.getYearGroup());
        } else {
            System.out.println("The student provided has not been added to the database");
        }
        conn.close();
        System.out.println("Returning to previous section");
    }

    private void removeStudent() throws Exception {
        System.out.println("Enter unique school Student ID [number]");
        int studentID = sc.nextInt();
        DatabaseConnect conn = new DatabaseConnect();
        if (conn.studentInDatabase(studentID)) {
            conn.removeStudent(studentID);
        } else {
            System.out.println("The student provided has not been added to the database");
        }
        conn.close();
        System.out.println("Returning to previous section");
    }

    private void editStudent() throws Exception {
        System.out.println("Enter unique school Student ID [number]");
        int studentID = sc.nextInt();
        DatabaseConnect conn = new DatabaseConnect();
        if (!conn.studentInDatabase(studentID)) {
            System.out.println("The student does not exist in the database");
        } else {
            System.out.println("CURRENT INFORMATION:");
            Student student = conn.getStudent(studentID);
            System.out.println("Student Name : " + student.getStudentName());
            System.out.println("Student year group : " + student.getYearGroup());
            conn.close();
            int response;
            do {
                System.out.println("1 -> Edit Name");
                System.out.println("2 -> Edit Year Group");
                System.out.println("3 -> Return");
                response = sc.nextInt();
                switch (response) {
                    case 1 -> editStudentName(studentID);
                    case 2 -> editStudentYearGroup(studentID);
                    case 3 -> System.out.println("Returning to previous section");
                    default -> System.out.println("Invalid input, try again.");
                }
            } while (response != 3);
        }
    }

    private void editStudentYearGroup(int studentID) throws Exception {
        System.out.println("Enter new Student Year Group -> ");
        int yearGroup = sc.nextInt();
        while (yearGroup != 7 && yearGroup != 8 && yearGroup != 9 && yearGroup != 10 && yearGroup != 11) {
            System.out.println("Invalid year group entered");
            System.out.println("Enter the current year group of the student");
            yearGroup = sc.nextInt();
        }
        DatabaseConnect conn = new DatabaseConnect();
        conn.editStudentYearStartedY7(studentID, yearGroupToYearStartedY7(yearGroup));
        conn.close();
    }

    private void editStudentName(int studentID) throws Exception {
        System.out.println("Enter new Student Name -> ");
        String newName = sc.nextLine().trim();
        DatabaseConnect conn = new DatabaseConnect();
        conn.editStudentName(studentID, newName);
        conn.close();
    }

    private void addStudent() throws Exception {
        System.out.println("Enter unique school Student ID [number]");
        int studentID = sc.nextInt();
        System.out.println("Enter student's full name");
        String studentName = sc.nextLine().trim();
        System.out.println("Enter the current year group of the student");
        int yearGroup = sc.nextInt();
        int yearStartedY7;
        while (yearGroup != 7 && yearGroup != 8 && yearGroup != 9 && yearGroup != 10 && yearGroup != 11) {
            System.out.println("Invalid year group entered");
            System.out.println("Enter the current year group of the student");
            yearGroup = sc.nextInt();
        }
        yearStartedY7 = yearGroupToYearStartedY7(yearGroup);
        DatabaseConnect conn = new DatabaseConnect();
        if (!conn.studentInDatabase(studentID)) {
            conn.addStudent(studentID, studentName, yearStartedY7);
        } else {
            System.out.println("This student is already in the database");
        }
        conn.close();
        System.out.println("Returning to previous section");
    }

    private int yearGroupToYearStartedY7(int yearGroup) {
        LocalDate date = LocalDate.now();
        if (date.getMonthValue() >= 9) {
            return date.getYear() - (yearGroup - 7);
        }
        return date.getYear() - (yearGroup - 6);

    }
}
