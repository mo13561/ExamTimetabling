public class ConstructTimetable {
    private Exam[] exams;
    private ConflictNode[][] TRC;

    public Exam[][] getInitialSolution() throws Exception {
        this.exams = getExams();
        this.exams = sort(this.exams, 0, this.exams.length - 1); //sort by enrolment (merge sort)
        TRC = getTRC();
        Exam[][] assignedExams = new Exam[TRC.length][];
        for (int i = 0; i < TRC.length; i++){
            LinkedList<Exam> workingSet = getUnsetExams();
            LinkedList<Exam> currentTimeslotExams = new LinkedList<>();
            while (workingSet.len() > 0 && roomsAvailable(TRC[i])) {
                int roomCounter = 0;
                boolean examSet = false;
                Exam exam = workingSet.getValue(0); //largest enrolment???
                while (!examSet && roomCounter <= TRC[i].length) {
                    ConflictNode timeRoom = TRC[i][roomCounter];
                    if (timeRoom.isAvailable() && examConstraintsSatisfied(exam, timeRoom)) {
                        timeRoom.setAvailable(false);
                        exam.setTimeslot(timeRoom.getTimeslot());
                        exam.setExamSet(true);
                        currentTimeslotExams.append(exam);
                        examSet = true;
                    }
                    roomCounter++;
                }
                if (examSet) {
                    Exam[] conflictingExams = getConflictingExams(exam);
                    for (int j = 0; j < conflictingExams.length; j++) {
                        workingSet.remove(conflictingExams[j]);
                    }
                }
                workingSet.remove(exam);
            }
            assignedExams[i] = new Exam[currentTimeslotExams.len()];
            for (int j = 0; j < assignedExams[i].length; j++) {
                assignedExams[i][j] = currentTimeslotExams.getValue(j);
            }
            if (getUnsetExams().isEmpty())
                break;
        }
        if (!getUnsetExams().isEmpty()) {
            System.out.println("No solution is possible given the available resources");
            return null;
        }
        return assignedExams;
    }

    private Exam[] getConflictingExams(Exam exam) throws Exception {
        LinkedList<Exam> tempConflictingExams = new LinkedList<>();
        for (int i = 0; i < this.exams.length; i++) {
            if (this.exams[i].getExamID() == exam.getExamID())
                continue;
            boolean conflictFlag = false;
            int[] students = this.exams[i].getStudents();
            for (int j = 0; j < students.length; j++) {
                for (int k = 0; k < exam.enrolment(); k++) {
                    if (exam.getStudents()[k] == students[j]) {
                        tempConflictingExams.append(this.exams[i]);
                        conflictFlag = true;
                        break;
                    }
                }
                if (conflictFlag)
                    break;
            }
        }
        Exam[] conflictingExams = new Exam[tempConflictingExams.len()];
        for (int i = 0; i < tempConflictingExams.len(); i++) {
            conflictingExams[i] = tempConflictingExams.getValue(i);
        }
        return conflictingExams;
    }

    private boolean examConstraintsSatisfied(Exam exam, ConflictNode timeRoom) {
        return (exam.enrolment() <= timeRoom.getRoom().getCapacity() //THERE IS ENOUGH SPACE IN ROOM
                && timeRoom.isAvailable() //THE ROOM IS AVAILABLE IN THE TIMESLOT
                && exam.getRequiredRoomType().equals(timeRoom.getRoom().getType())); //THE ROOM TYPE IS CORRECT FOR THE EXAM
    }

    private boolean roomsAvailable(ConflictNode[] timeslot) {
        for (int i = 0; i < timeslot.length; i++) {
            if (timeslot[i].isAvailable())
                return true;
        }
        return false;
    }

    private LinkedList<Exam> getUnsetExams() throws Exception {
        LinkedList<Exam> unsetExams = new LinkedList<>();
        for (int i = 0; i < this.exams.length; i++) {
            if (!this.exams[i].isExamSet()) {
                unsetExams.append(this.exams[i]);
            }
        }
        return unsetExams;
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
