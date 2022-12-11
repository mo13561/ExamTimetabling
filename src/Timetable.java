public class Timetable {
    private Exam[][] timetable;

    public boolean makeTimetable() { //returns boolean for if construction was successful
        ConstructTimetable constructTimetable;
        this.timetable = new Exam[0][];
        try {
            constructTimetable = new ConstructTimetable();
            timetable = constructTimetable.constructTimetable(); //stages 1 and 2 for construction
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (timetable == null) {
            System.out.println("Unable to construct a timetable with given resources.");
            return false;
        }
        if (!addInvigilators()) { //adding invigilators to timetable (stage 3)
            return false;
        }
        addToDatabase(); //adding timetable to database
        return true;
    }

    private void addToDatabase() { //adding the timetable to the database
        DatabaseConnect conn = new DatabaseConnect();
        try {
            conn.addTimetable(timetable);
        } catch (Exception e) {
            e.printStackTrace();
        }
        conn.close();
    }

    public boolean addInvigilators() { //adding invigilators to timetable after stages 1,2,3 are completed.
        ConstructInvigilatorTimetable invTable = new ConstructInvigilatorTimetable(this.timetable);
        try {
            this.timetable = invTable.addInvigilators();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.timetable != null;
    }
}
