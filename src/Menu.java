import java.time.LocalDate;
import java.util.Scanner;

public class Menu {
    Scanner sc = new Scanner(System.in);

    public void mainLoop() {
        int response;
        do {
            System.out.println("1 -> Student Management");
            System.out.println("2 -> Invigilator Management");
            System.out.println("3 -> Timetable Production"); //TODO
            System.out.println("4 -> Class Management");
            System.out.println("5 -> Timeslot Management"); //TODO
            System.out.println("6 -> Room Management"); //TODO
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
        Menu menu = new Menu();
        menu.mainLoop();
    }

    private void roomManagement() {

    }

    private void timeslotManagement() {

    }

    private void classManagement() {
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

    private void getClassInformation() {

    }

    private void removeClass() {
        System.out.println("Enter unique Class ID [number]");
        int classID = sc.nextInt();
        DatabaseConnect conn = new DatabaseConnect();
        try {
            if (conn.classInDatabase(classID)) {
                conn.removeClass(classID);
            } else {
                System.out.println("The class provided has not been added to the database");
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Returning to previous section");
    }

    private void editClass() {
        boolean classExists = false;
        System.out.println("Enter class ID of class to be edited -> ");
        int classID = sc.nextInt();
        DatabaseConnect conn = new DatabaseConnect();
        try {
            classExists = conn.classInDatabase(classID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (classExists) {
            System.out.println("CURRENT INFORMATION:");
            try {
                SClass sClass = conn.getSClass(classID);
                System.out.println("Class ID : " + sClass.getClassID());
                System.out.println("Class subject type : " + sClass.getClassType());
                System.out.println("Class year group : " + sClass.getYearGroup());
                System.out.println("Students enrolled (IDs) : " + printStudents(conn.getStudentsOfClasses(classID)));
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    private void editClassYearGroup(int classID) {
        System.out.println("Enter new class year group -> ");
        int yearGroup = sc.nextInt();
        while (yearGroup < 7 || yearGroup > 11) {
            System.out.println("Invalid year group entered");
            System.out.println("Enter new class year group -> ");
            yearGroup = sc.nextInt();
        }
        DatabaseConnect conn = new DatabaseConnect();
        try {
            conn.editClassYearGroup(classID, yearGroup);
        } catch (Exception e) {
            e.printStackTrace();
        }
        conn.close();
    }

    private void editClassEnrolment(int classID) {
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

    private void removeStudentFromClass(int classID) {
        System.out.println("Enter unique school Student ID [number] ->");
        int studentID = sc.nextInt();
        DatabaseConnect conn = new DatabaseConnect();
        try {
            if (conn.studentInDatabase(studentID)) {
                conn.removeStudentFromClass(studentID, classID);
            } else {
                System.out.println("The student provided has not been added to the database");
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Returning to previous section");
    }

    private void editClassType(int classID) {
        System.out.println("Enter new class type -> ");
        String classType = sc.nextLine().trim();
        DatabaseConnect conn = new DatabaseConnect();
        try {
            conn.editClassType(classID, classType);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void addClass() {
        System.out.println("Enter class ID -> ");
        int classID = sc.nextInt();
        DatabaseConnect conn = new DatabaseConnect();
        try {
            while (conn.classInDatabase(classID)) {
                System.out.println("Enter class ID -> ");
                classID = sc.nextInt();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Enter class subject type -> ");
        String type = sc.nextLine().trim();
        System.out.println("Enter class year group -> ");
        int yearGroup = sc.nextInt();
        while (yearGroup < 7 || yearGroup > 11) {
            System.out.println("Invalid year group entered");
            System.out.println("Enter class year group -> ");
            yearGroup = sc.nextInt();
        }
        try {
            conn.addClass(classID, type, yearGroup);
        } catch (Exception e) {
            e.printStackTrace();
        }
        conn.close();
        enrolStudents(classID);
        System.out.println("Returning to previous section");
    }

    private void enrolStudents(int classID) {
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

    private void enrolStudent(int classID) {
        System.out.println("Enter student ID -> ");
        int studentID = sc.nextInt();
        DatabaseConnect conn = new DatabaseConnect();
        try {
            if (conn.studentInDatabase(studentID)) {
                conn.enrolStudentInClass(classID, studentID);
            } else {
                System.out.println("The student provided has not been added to the database, try again");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        conn.close();
    }

    private void timetableProduction() {

    }

    private void invigilatorManagement() {
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

    private void getInvigilatorInformation() {
        System.out.println("Enter unique Invigilator ID [number]");
        int invigilatorID = sc.nextInt();
        DatabaseConnect conn = new DatabaseConnect();
        try {
            if (conn.invigilatorInDatabase(invigilatorID)) {
                Invigilator invigilator = conn.getInvigilator(invigilatorID);
                conn.close();
                System.out.println("Invigilator ID : " + invigilator.getInvID());
                System.out.println("Invigilator contracted exams left : " + invigilator.getExamsLeft());
            } else {
                System.out.println("The invigilator provided has not been added to the database");
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Returning to previous section");
    }

    private void removeInvigilator() {
        System.out.println("Enter unique Invigilator ID [number]");
        int invigilatorID = sc.nextInt();
        DatabaseConnect conn = new DatabaseConnect();
        try {
            if (conn.invigilatorInDatabase(invigilatorID)) {
                conn.removeInvigilator(invigilatorID);
            } else {
                System.out.println("The invigilator provided has not been added to the database");
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Returning to previous section");
    }

    private void editInvigilator() {
        System.out.println("Enter unique Invigilator ID [number]");
        int invigilatorID = sc.nextInt();
        DatabaseConnect conn = new DatabaseConnect();
        try {
            if (!conn.invigilatorInDatabase(invigilatorID)) {
                System.out.println("The invigilator does not exist in the database");
            } else {
                System.out.println("CURRENT INFORMATION:");
                try {
                    Invigilator invigilator = conn.getInvigilator(invigilatorID);
                    System.out.println("Invigilator ID : " + invigilator.getInvID());
                    System.out.println("Invigilator contracted exams left : " + invigilator.getExamsLeft());
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Returning to previous section");
    }

    private void editInvigilatorExamsLeft(int invigilatorID) {
        System.out.println("Enter new contracted exams left -> ");
        int newExamsLeft = sc.nextInt();
        DatabaseConnect conn = new DatabaseConnect();
        try {
            conn.editInvigilatorExamsLeft(invigilatorID, newExamsLeft);
        } catch (Exception e) {
            e.printStackTrace();
        }
        conn.close();
    }

    private void addInvigilator() {
        System.out.println("Enter unique Invigilator ID [number]");
        int invigilatorID = sc.nextInt();
        System.out.println("Enter contracted exams left [number]");
        int examsLeft = sc.nextInt();
        try {
            DatabaseConnect conn = new DatabaseConnect();
            if (!conn.invigilatorInDatabase(invigilatorID)) {
                conn.addInvigilator(invigilatorID, examsLeft);
            } else {
                System.out.println("This invigilator is already in the database");
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Returning to previous section");
    }

    private void studentManagement() {
        int response;
        do {
            System.out.println("1 -> Add Student");
            System.out.println("2 -> Edit Student");
            System.out.println("3 -> Remove Student");
            System.out.println("4 -> Get Student Information");
            System.out.println("5 -> Return");
            response = sc.nextInt();
            switch (response) {
                case 1 -> addStudent();
                case 2 -> editStudent();
                case 3 -> removeStudent();
                case 4 -> getStudentInformation();
                case 5 -> System.out.println("Returning to previous section");
                default -> System.out.println("Invalid input, try again.");
            }
        } while (response != 5);
    }

    private void getStudentInformation() {
        System.out.println("Enter unique school Student ID [number]");
        int studentID = sc.nextInt();
        DatabaseConnect conn = new DatabaseConnect();
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Returning to previous section");
    }

    private void removeStudent() {
        System.out.println("Enter unique school Student ID [number]");
        int studentID = sc.nextInt();
        DatabaseConnect conn = new DatabaseConnect();
        try {
            if (conn.studentInDatabase(studentID)) {
                conn.removeStudent(studentID);
            } else {
                System.out.println("The student provided has not been added to the database");
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Returning to previous section");
    }

    private void editStudent() {
        System.out.println("Enter unique school Student ID [number]");
        int studentID = sc.nextInt();
        DatabaseConnect conn = new DatabaseConnect();
        try {
            if (!conn.studentInDatabase(studentID)) {
                System.out.println("The student does not exist in the database");
            } else {
                System.out.println("CURRENT INFORMATION:");
                try {
                    Student student = conn.getStudent(studentID);
                    System.out.println("Student Name : " + student.getStudentName());
                    System.out.println("Student year group : " + student.getYearGroup());
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void editStudentYearGroup(int studentID) {
        System.out.println("Enter new Student Year Group -> ");
        int yearGroup = sc.nextInt();
        while (yearGroup != 7 && yearGroup != 8 && yearGroup != 9 && yearGroup != 10 && yearGroup != 11) {
            System.out.println("Invalid year group entered");
            System.out.println("Enter the current year group of the student");
            yearGroup = sc.nextInt();
        }
        DatabaseConnect conn = new DatabaseConnect();
        try {
            conn.editStudentYearStartedY7(studentID, yearGroupToYearStartedY7(yearGroup));
        } catch (Exception e) {
            e.printStackTrace();
        }
        conn.close();
    }

    private void editStudentName(int studentID) {
        System.out.println("Enter new Student Name -> ");
        String newName = sc.nextLine().trim();
        DatabaseConnect conn = new DatabaseConnect();
        try {
            conn.editStudentName(studentID, newName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        conn.close();
    }

    private void addStudent() {
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
        try {
            DatabaseConnect conn = new DatabaseConnect();
            if (!conn.studentInDatabase(studentID)) {
                conn.addStudent(studentID, studentName, yearStartedY7);
            } else {
                System.out.println("This student is already in the database");
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
