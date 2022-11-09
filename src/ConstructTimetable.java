public class ConstructTimetable {
    private Exam[] exams;
    private ConflictNode[][] TRC;

    public void getInitialSolution() throws Exception {
        this.exams = getExams();
        this.exams = sort(this.exams, 0, this.exams.length - 1);
        TRC = getTRC();
    }

    private ConflictNode[][] getTRC() throws Exception {
        DatabaseConnect connect = new DatabaseConnect();
        return connect.getTRC();
    }

    private Exam[] getExams() throws Exception {
        DatabaseConnect connect = new DatabaseConnect();
        return connect.getAllExams();
    }

    private void merge(Exam[] arr, int l, int middle, int r) throws Exception {
        int length1 = middle - l + 1;
        int length2 = r - middle;

        LinkedList<Exam> left = new LinkedList<>();
        LinkedList<Exam> right = new LinkedList<>();
        for (int i = 0; i < length1; i++)
            left.append(arr[l + i]);
        for (int i = 0; i < length2; i++)
            right.append(arr[middle + i + 1]);

        int i = 0, j = 0, k = l;

        while (i < length1 && j < length2) {
            if (left.getValue(i).enrolment() <= right.getValue(j).enrolment()) {
                arr[k] = left.getValue(i);
                i++;
            }
            else {
                arr[k] = right.getValue(j);
                j++;
            }
            k++;
        }
        while (i < length1) {
            arr[k] = left.getValue(i);
            i++;
            k++;
        }
        while (j < length2) {
            arr[k] = right.getValue(j);
            j++;
            k++;
        }
    }

    public Exam[] sort(Exam[] arr, int start, int end) throws Exception {
        if (start < end) {
            int middle = start + (end - start) / 2;
            sort(arr, start, middle);
            sort(arr, middle + 1, end);
            merge(arr, start, middle, end);
        }
        return arr;
    }
}
