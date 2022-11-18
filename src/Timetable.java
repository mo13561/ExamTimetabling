public class Timetable {
    private Exam[][] timetable;

    public void makeTimetable() {
        ConstructTimetable constructTimetable;
        this.timetable = new Exam[0][];
        try {
            constructTimetable = new ConstructTimetable();
            System.out.println("constructed");
            timetable = constructTimetable.constructTimetable();
            System.out.println("built timetable");
        } catch (Exception e) {
            e.printStackTrace();
        }
        addInvigilators();
        System.out.println("added invigilators");
        addToDatabase();
        System.out.println("added to database");
        System.out.println("done finally");
    }

    private void addToDatabase() {
        DatabaseConnect conn = new DatabaseConnect();
        try {
            conn.addTimetable(timetable);
        } catch (Exception e) {
            e.printStackTrace();
        }
        conn.close();
    }

    public void addInvigilators() {
        ConstructInvigilatorTimetable invTable = new ConstructInvigilatorTimetable(this.timetable);
        try {
            this.timetable = invTable.addInvigilators();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
