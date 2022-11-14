import java.util.Arrays;

public class Runner {
    public static void main(String[] args) {
        ConstructTimetable constructTimetable;
        Exam[][] timetable = new Exam[0][];
        try {
            constructTimetable = new ConstructTimetable();
            System.out.println("constructed");
            timetable = constructTimetable.constructTimetable();
            System.out.println("built timetable");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(Arrays.deepToString(timetable));
//        DatabaseConnect conn = new DatabaseConnect();
//        conn.addTimetable(timetable);
//        conn.close();
        System.out.println("done finally");
    }
}
