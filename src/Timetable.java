public class Timetable {
    private Exam[][] timetable;

    public boolean makeTimetable() {
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
        if (timetable == null) {
            System.out.println("Unable to construct a timetable with given resources.");
            return false;
        }
        if (!addInvigilators()) {
            return false;
        }
        System.out.println("added invigilators");
        addToDatabase();
        System.out.println("added to database");
        return true;
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

    public boolean addInvigilators() {
        ConstructInvigilatorTimetable invTable = new ConstructInvigilatorTimetable(this.timetable);
        try {
            this.timetable = invTable.addInvigilators();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.timetable != null;
    }
}
