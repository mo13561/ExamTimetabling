public class ConstructTimetable {
    private Exam[] exams;
    private ConflictNode[][] TRC;

    private Exam[] getConflictingExams(Exam exam) throws Exception {
        LinkedList<Exam> tempConflictingExams = new LinkedList<>();
        for (Exam value : this.exams) {
            if (value.getExamID() == exam.getExamID())
                continue;
            boolean conflictFlag = false;
            int[] students = value.getStudents();
            for (int student : students) {
                for (int k = 0; k < exam.enrolment(); k++) {
                    if (exam.getStudents()[k] == student) {
                        tempConflictingExams.append(value);
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

    private Exam[][] getInitialSolution() throws Exception {
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
                    for (Exam conflictingExam : conflictingExams) {
                        workingSet.remove(conflictingExam);
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

    private Exam[][] localSearch(Exam[][] INIT_SOLUTION) {
        int tenureShort = 9;
        int maxLong = 4;
        Exam[][] bestSol = INIT_SOLUTION;
        Exam[][] currentSol = INIT_SOLUTION;
        double fBest = costFunction(INIT_SOLUTION);
        LinkedList<Move> shortTB = new LinkedList<>();
        Move[] longTB = new Move[maxLong];
        Hashmap<Exam, Integer> mvf = new Hashmap<>();
        int iterNum = 0;
        int bestIter = 0;
        int nullIter = 7;
        while (costFunction(bestSol) > 0 && (iterNum - bestIter) < nullIter) {
            iterNum++;
            Move[] Neighbourhood = getNeighbours(currentSol, shortTB, longTB, mvf, fBest);
        }
        return new Exam[0][];
    }

    private Move[] getNeighbours(Exam[][] currentSol, LinkedList<Move> shortTB, Move[] longTB, Hashmap<Exam, Integer> mvf, double fBest) {
        //TODO
        return new Move[0];
    }

    private double costFunction(Exam[][] init_solution) {
        //TODO
        return 0;
    }

    private boolean examConstraintsSatisfied(Exam exam, ConflictNode timeRoom) {
        return (exam.enrolment() <= timeRoom.getRoom().getCapacity() //THERE IS ENOUGH SPACE IN ROOM
                && timeRoom.isAvailable() //THE ROOM IS AVAILABLE IN THE TIMESLOT
                && exam.getRequiredRoomType().equals(timeRoom.getRoom().getType())); //THE ROOM TYPE IS CORRECT FOR THE EXAM
    }

    private boolean roomsAvailable(ConflictNode[] timeslot) {
        for (ConflictNode conflictNode : timeslot) {
            if (conflictNode.isAvailable())
                return true;
        }
        return false;
    }

    private LinkedList<Exam> getUnsetExams() {
        LinkedList<Exam> unsetExams = new LinkedList<>();
        for (Exam exam : this.exams) {
            if (!exam.isExamSet()) {
                unsetExams.append(exam);
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
