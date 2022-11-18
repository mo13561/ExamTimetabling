public class Timetable {
    Exam[][] timetable;

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
        System.out.println("done finally");
    }
}
