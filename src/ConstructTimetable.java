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
                        exam.setRoom(timeRoom.getRoom());
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
    /*TODO: change short tabu to pair with move tenure
    * TODO: update MVF
    * TODO: update long tabu
    * TODO: IF BEST --> duplate current sol into best sol
    * */

    private Exam[][] localSearch(Exam[][] INIT_SOLUTION) throws Exception {
        int tenureShort = 9;
        int maxLong = 4;
        Exam[][] bestSol = INIT_SOLUTION;
        Exam[][] currentSol = INIT_SOLUTION;
        double fBest = costFunction(INIT_SOLUTION);
        LinkedList<Move> shortTB = new LinkedList<>(); //regard move as [exam, timeslot exam is from, room exam is from]
        Move[] longTB = new Move[maxLong];
        Hashmap<Exam, Integer> mvf = new Hashmap<>();
        int iterNum = 0;
        int bestIter = 0;
        int nullIter = 7;
        while (costFunction(bestSol) > 0 && (iterNum - bestIter) < nullIter) {
            iterNum++;
            LinkedList<Swap> swaps = new LinkedList<>();
            LinkedList<Move> moves = new LinkedList<>();
            Exam[][] newSol = new Exam[currentSol.length][];
            for (int i = 0; i < this.exams.length; i++) {
                for (int j = i + 1; j < this.exams.length; j++) {
                    Exam exam1 = this.exams[i];
                    Exam exam2 = this.exams[j];
                    if (exam1.enrolment() <= exam2.getRoom().getCapacity() && exam1.getRequiredRoomType().equals(exam2.getRequiredRoomType())
                            && moveNotInLongTB(longTB, exam1) && moveNotInLongTB(longTB, exam2)) {
                        swaps.append(new Swap(exam1, exam2));
                    }
                }
            }
            for (ConflictNode[] timeslot : TRC) {
                for (ConflictNode timeRoom : timeslot) {
                    if (!timeRoom.isAvailable())
                        continue;
                    for (Exam exam : this.exams) {
                        if (exam.enrolment() <= timeRoom.getRoom().getCapacity() && exam.getRequiredRoomType().equals(timeRoom.getRoom().getType()) && moveNotInLongTB(longTB, exam)) {
                            moves.append(new Move(exam, timeRoom.getTimeslot(), timeRoom.getRoom()));
                        }
                    }
                }
            }
            for (int i = 0; i < swaps.len(); i++) {
                if (!swapNotInShortTB(swaps.getValue(i), shortTB) && getSwapCost(currentSol, swaps.getValue(i)) > fBest) {
                    swaps.remove(swaps.getValue(i));
                    i--;
                }
            }
            Swap bestSwap = null;
            int bestSwapCost = -1;
            if (!swaps.isEmpty()) {
                bestSwap = swaps.getValue(0);
                bestSwapCost = getSwapCost(currentSol, bestSwap);
                for (int i = 1; i < swaps.len(); i++) {
                    int currentSwapCost = getSwapCost(currentSol, swaps.getValue(i));
                    if (currentSwapCost < bestSwapCost) {
                        bestSwap = swaps.getValue(i);
                        bestSwapCost = currentSwapCost;
                    }
                }
            }
            for (int i = 0; i < moves.len(); i++) {
                if (!moveNotInShortTB(shortTB, moves.getValue(i).getExam()) && getMoveCost(currentSol, moves.getValue(i)) > fBest) {
                    moves.remove(swaps.getValue(i));
                    i--;
                }
            }
            Move bestMove = null;
            int bestMoveCost = -1;
            if (!moves.isEmpty()) {
                bestMove = moves.getValue(0);
                bestMoveCost = getMoveCost(currentSol, bestMove);
                for (int i = 1; i < swaps.len(); i++) {
                    int currentMoveCost = getMoveCost(currentSol, moves.getValue(i));
                    if (currentMoveCost < bestMoveCost) {
                        bestMove = swaps.getValue(i);
                        bestMoveCost = currentMoveCost;
                    }
                }
            }
            if (bestMoveCost <= bestSwapCost && bestMove != null) { //we do a move
                int timeFrom = 0;
                int timeTo = 0;
                for (int i = 0; i < TRC.length; i++) {
                    if (TRC[i][0].getTimeslot().getWeekNum() == bestMove.getTimeslotFrom().getWeekNum() && TRC[i][0].getTimeslot().getPeriodNum() == bestMove.getTimeslotFrom().getPeriodNum()) {
                        timeFrom = i;
                    }
                    if (TRC[i][0].getTimeslot().getWeekNum() == bestMove.getTimeslotTo().getWeekNum() && TRC[i][0].getTimeslot().getPeriodNum() == bestMove.getTimeslotTo().getPeriodNum()) {
                        timeTo = i;
                    }
                }
                for (int i = 0; i < currentSol[timeFrom].length; i++) {
                    if (currentSol[timeFrom][i].getExamID() == bestMove.getExam().getExamID()) {
                        Exam examFrom = currentSol[timeFrom][i];
                        examFrom.setTimeslot(bestMove.getTimeslotTo());
                        LinkedList<Exam> tempExamsFrom = new LinkedList<>();
                        for (int j = 0; j < currentSol[timeFrom].length; j++) {
                            if (j != i) {
                                tempExamsFrom.append(currentSol[timeFrom][j]);
                            }
                        }
                        Exam[] tempExamCollection = new Exam[tempExamsFrom.len()];
                        for (int j = 0; j < tempExamCollection.length; j++) {
                            tempExamCollection[j] = tempExamsFrom.getValue(i);
                        }
                        currentSol[timeFrom] = tempExamCollection;
                        Exam[] tempExamCollection2 = new Exam[currentSol[timeTo].length + 1];
                        System.arraycopy(currentSol[timeTo],0,tempExamCollection2,0,currentSol[timeTo].length);
                        tempExamCollection2[tempExamCollection2.length - 1] = examFrom;
                        currentSol[timeTo] = tempExamCollection2;
                        for (int j = 0; j < TRC[timeFrom].length; j++) {
                            if (TRC[timeFrom][j].getRoom().getRoomID() == bestMove.getRoomFrom().getRoomID()) {
                                TRC[timeFrom][j].setAvailable(true);
                                break;
                            }
                        }
                        for (int j = 0; j < TRC[timeTo].length; j++) {
                            if (TRC[timeTo][j].getRoom().getRoomID() == bestMove.getRoomTo().getRoomID()) {
                                TRC[timeTo][j].setAvailable(false);
                                break;
                            }
                        }
                    }
                }
            } else if (bestMoveCost > bestSwapCost && bestSwap != null) { //we do a swap
                int timeFrom = 0;
                int timeTo = 0;
                for (int i = 0; i < TRC.length; i++) {
                    if (TRC[i][0].getTimeslot().getWeekNum() == bestSwap.getTimeslotFrom().getWeekNum() && TRC[i][0].getTimeslot().getPeriodNum() == bestSwap.getTimeslotFrom().getPeriodNum()) {
                        timeFrom = i;
                    }
                    if (TRC[i][0].getTimeslot().getWeekNum() == bestSwap.getTimeslotTo().getWeekNum() && TRC[i][0].getTimeslot().getPeriodNum() == bestSwap.getTimeslotTo().getPeriodNum()) {
                        timeTo = i;
                    }
                }
                for (int i = 0; i < currentSol[timeFrom].length; i++) {
                    if (currentSol[timeFrom][i].getExamID() == bestSwap.getExam().getExamID()) {
                        for (int j = 0; j < currentSol[timeTo].length; j++) {
                            if (currentSol[timeTo][j].getExamID() == bestSwap.getExam2().getExamID()) {
                                currentSol[timeFrom][i].setTimeslot(bestSwap.getTimeslotTo());
                                currentSol[timeTo][j].setTimeslot(bestSwap.getTimeslotFrom());
                                currentSol[timeFrom][i].setRoom(bestSwap.getRoomTo());
                                currentSol[timeTo][j].setRoom(bestSwap.getRoomFrom());
                                currentSol[timeFrom][i] = currentSol[timeFrom][j];
                                currentSol[timeFrom][j] = bestSwap.getExam();
                            }
                        }
                    }
                }
            }

            for (int i = 0; i < shortTB.len(); i++) {
                //TODO
            }
        }
        return new Exam[0][];
    }

    private int getMoveCost(Exam[][] currentSol, Move bestMove) {
        //TODO
        return 0;
    }

    private int getSwapCost(Exam[][] currentSol, Swap value) {
        //TODO
        return 0;
    }

    private double costFunction(Exam[][] solution) {
        //TODO
        return 0;
    }

    private boolean swapNotInShortTB(Swap swap, LinkedList<Move> shortTB) throws Exception {
        return moveNotInShortTB(shortTB, swap.getExam()) && moveNotInShortTB(shortTB, swap.getExam2());
    }

    private boolean moveNotInLongTB(Move[] longTB, Exam exam1) {
        for (Move move : longTB) {
            if (move.getExam().getExamID() == exam1.getExamID() && move.getRoomFrom().getRoomID() == exam1.getRoom().getRoomID()
                    && move.getTimeslotFrom().getPeriodNum() == exam1.getPeriodNum() && move.getTimeslotFrom().getWeekNum() == exam1.getWeekNum()) {
                return false;
            }
        }
        return true;
    }

    private boolean moveNotInShortTB(LinkedList<Move> shortTB, Exam exam1) throws Exception {
        for (int i = 0; i < shortTB.len(); i++) {
            if (shortTB.getValue(i).getExam().getExamID() == exam1.getExamID() && shortTB.getValue(i).getRoomFrom().getRoomID() == exam1.getRoom().getRoomID()
                    && shortTB.getValue(i).getTimeslotFrom().getPeriodNum() == exam1.getPeriodNum() && shortTB.getValue(i).getTimeslotFrom().getWeekNum() == exam1.getWeekNum()) {
                return false;
            }
        }
        return true;
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
