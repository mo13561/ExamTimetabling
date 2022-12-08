public class ConstructTimetable {
    private Exam[] exams;
    private final ConflictNode[][] TRC;

    public ConstructTimetable() throws Exception {
        getExams();
        sort(this.exams, 0, this.exams.length - 1);//sort by enrolment (merge sort)
        this.TRC = getTRC();
    }

    public Exam[][] constructTimetable() throws Exception {
        Exam[][] solution;
        try {
            solution = getInitialSolution();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return localSearch(solution);
    }

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
        Exam[][] assignedExams = new Exam[TRC.length][];
        for (int i = 0; i < TRC.length; i++){
            LinkedList<Exam> workingSet = getUnsetExams();
            LinkedList<Exam> currentTimeslotExams = new LinkedList<>();
            while (workingSet.len() > 0 && roomsAvailable(TRC[i])) {
                int roomCounter = 0;
                boolean examSet = false;
                Exam exam = workingSet.getValue(0); //largest enrolment
                while (!examSet && roomCounter < TRC[i].length) {
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
                        if (workingSet.contains(conflictingExam))
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
            throw new Exception("No solution is possible given the available resources");
        }
        return assignedExams;
    }

    private Exam[][] localSearch(Exam[][] currentSol) throws Exception {
        int tenureShort = 9;
        int maxLong = 4;
        Exam[][] bestSol = deepDuplicateCurrentSol(currentSol);
        double fBest = costFunction(currentSol);
        double fCurrent = fBest;
        LinkedList<MoveTenure> shortTB = new LinkedList<>(); //regard move as [exam, timeslot exam is from, room exam is from]
        Exam[] longTB = new Exam[maxLong];
        Hashmap<Integer, Integer> mvf = new Hashmap<>();
        int iterNum = 0;
        int bestIter = 0;
        int nullIter = 7;
        while (costFunction(bestSol) > 0 && (iterNum - bestIter) < nullIter) {
            iterNum++;
            LinkedList<Swap> swaps = new LinkedList<>();
            LinkedList<Move> moves = new LinkedList<>();
            getSwaps(swaps, shortTB, longTB, currentSol, fBest);
            getMoves(moves, shortTB, longTB, currentSol, fBest);
            if (swaps.len() == 0 && moves.len() == 0) { //no more moves possible so stop local search
                iterNum = Integer.MAX_VALUE;
                continue;
            }
            Swap bestSwap = null; //get best swap
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
            Move bestMove = null; //get best move
            int bestMoveCost = -1;
            if (!moves.isEmpty()) {
                bestMove = moves.getValue(0);
                bestMoveCost = getMoveCost(currentSol, bestMove);
                for (int i = 1; i < moves.len(); i++) {
                    int currentMoveCost = getMoveCost(currentSol, moves.getValue(i));
                    if (currentMoveCost < bestMoveCost) {
                        bestMove = moves.getValue(i);
                        bestMoveCost = currentMoveCost;
                    }
                }
            }
            if (bestMoveCost < bestSwapCost && bestMove != null) { //we do a move
                updateShortTBWithMove(shortTB, bestMove, tenureShort);
                updateMVFWithExam(mvf, bestMove.getExam());
                updateLongTBWithMove(longTB, mvf, bestMove.getExam());
                fCurrent = bestMoveCost;
                int[] times = getTimesForMove(bestMove);
                int timeFrom = times[0];
                int timeTo = times[1];
                performMove(currentSol, timeFrom, timeTo, bestMove);
                for (int j = 0; j < TRC[timeFrom].length; j++) { //update room availability after move
                    if (TRC[timeFrom][j].getRoom().getRoomID() == bestMove.getRoomFrom().getRoomID()) {
                        TRC[timeFrom][j].setAvailable(true);
                        break;
                    }
                }
                for (int j = 0; j < TRC[timeTo].length; j++) { //we need to keep track of room availability for current solution!
                    if (TRC[timeTo][j].getRoom().getRoomID() == bestMove.getRoomTo().getRoomID()) {
                        TRC[timeTo][j].setAvailable(false);
                        break;
                    }
                }
            } else if (bestMoveCost >= bestSwapCost && bestSwap != null) { //we do a swap
                updateShortTBWithSwap(shortTB, bestSwap, tenureShort);
                updateMVFWithSwap(mvf, bestSwap);
                updateLongTBWithSwap(longTB, mvf, bestSwap);
                fCurrent = bestSwapCost;
                int[] times = getTimesForSwap(bestSwap);
                int timeFrom = times[0];
                int timeTo = times[1];
                performSwap(currentSol, bestSwap, timeFrom, timeTo);
            } //room availability remains same as we just swap what exams are happening in the rooms
            if (fCurrent < fBest) { //if current solution better than or as good as best solution
                bestSol = deepDuplicateCurrentSol(currentSol);
                fBest = fCurrent;
                bestIter = iterNum;
            } else if (fCurrent == fBest) {
                bestSol = deepDuplicateCurrentSol(currentSol);
            }
            System.out.println("iteration");
        }
        return bestSol;
    }

    private void performSwap(Exam[][] currentSol, Swap bestSwap, int timeFrom, int timeTo) {
        for (int i = 0; i < currentSol[timeFrom].length; i++) {
            if (currentSol[timeFrom][i].getExamID() == bestSwap.getExam().getExamID()) {
                for (int j = 0; j < currentSol[timeTo].length; j++) {//swapping the exams that are happening between two room/timeslot combinations
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

    private int[] getTimesForSwap(Swap bestSwap) {
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
        return new int[]{timeFrom, timeTo};
    }

    private void performMove(Exam[][] currentSol, int timeFrom, int timeTo, Move bestMove) throws Exception {
        for (int i = 0; i < currentSol[timeFrom].length; i++) {
            if (currentSol[timeFrom][i].getExamID() == bestMove.getExam().getExamID()) {
                Exam examFrom = currentSol[timeFrom][i];
                examFrom.setTimeslot(bestMove.getTimeslotTo());
                examFrom.setRoom(bestMove.getRoomTo());
                LinkedList<Exam> tempExamsFrom = new LinkedList<>();
                for (int j = 0; j < currentSol[timeFrom].length; j++) {
                    if (j != i) {
                        tempExamsFrom.append(currentSol[timeFrom][j]);
                    }
                }
                Exam[] tempExamCollection = new Exam[tempExamsFrom.len()];
                for (int j = 0; j < tempExamCollection.length; j++) {
                    tempExamCollection[j] = tempExamsFrom.getValue(j);
                }
                currentSol[timeFrom] = tempExamCollection;
                Exam[] tempExamCollection2 = new Exam[(currentSol[timeTo] == null) ? 1 : currentSol[timeTo].length + 1];
                if (currentSol[timeTo] == null) {
                    tempExamCollection2[0] = examFrom;
                } else {
                    System.arraycopy(currentSol[timeTo],0,tempExamCollection2,0,currentSol[timeTo].length);
                    tempExamCollection2[tempExamCollection2.length - 1] = examFrom;
                }
                currentSol[timeTo] = tempExamCollection2;
            }
        }
    }

    private void getMoves(LinkedList<Move> moves, LinkedList<MoveTenure> shortTB, Exam[] longTB, Exam[][] currentSol, double fBest) throws Exception {
        for (ConflictNode[] timeslot : TRC) { //searching for moves
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
        for (int i = 0; i < moves.len(); i++) {
            if (!moveNotInShortTB(shortTB, moves.getValue(i).getExam()) && getMoveCost(currentSol, moves.getValue(i)) > fBest) {
                moves.remove(moves.getValue(i));
                i--;
            }
        }
    }

    private void getSwaps(LinkedList<Swap> swaps, LinkedList<MoveTenure> shortTB, Exam[] longTB, Exam[][] currentSol, double fBest) throws Exception {
        for (int i = 0; i < this.exams.length; i++) { //searching for swaps
            for (int j = i + 1; j < this.exams.length; j++) {
                Exam exam1 = this.exams[i];
                Exam exam2 = this.exams[j];
                if (exam1.enrolment() <= exam2.getRoom().getCapacity() && exam1.getRequiredRoomType().equals(exam2.getRequiredRoomType())
                        && moveNotInLongTB(longTB, exam1) && moveNotInLongTB(longTB, exam2)) {
                    swaps.append(new Swap(exam1, exam2));
                }
            }
        }
        for (int i = 0; i < swaps.len(); i++) {
            if (!swapNotInShortTB(swaps.getValue(i), shortTB) && getSwapCost(currentSol, swaps.getValue(i)) > fBest) {
                swaps.remove(swaps.getValue(i));
                i--;
            }
        }
    }

    private Exam[][] deepDuplicateCurrentSol(Exam[][] currentSol) throws Exception {
        Exam[][] duplicate = new Exam[currentSol.length][];
        for (int i = 0; i < currentSol.length; i++) {
            if (currentSol[i] == null)
                continue;
            Exam[] tempTimeslot = new Exam[currentSol[i].length];
            for (int j = 0; j < currentSol[i].length; j++) {
                Exam original = currentSol[i][j];
                tempTimeslot[j] = new Exam(original.getExamID(), original.getExamSub(), original.getRequiredRoomType(),
                        original.getClasses(), original.getStudents(), original.getWeekNum(), original.getPeriodNum(), original.getRoom());
            }
            duplicate[i] = tempTimeslot;
        }
        return duplicate;
    }

    private void updateLongTBWithSwap(Exam[] longTB, Hashmap<Integer, Integer> mvf, Swap bestSwap) {
        updateLongTBWithMove(longTB, mvf, bestSwap.getExam());
        updateLongTBWithMove(longTB, mvf, bestSwap.getExam2());
    }

    private void updateLongTBWithMove(Exam[] longTB, Hashmap<Integer, Integer> mvf, Exam exam) {
        if (mvf.item(exam.getExamID()) > 2) {
            boolean isFull = true;
            for (int i = 0; i < longTB.length; i++) {
                if (longTB[i] == null) {
                    isFull = false;
                    longTB[i] = exam;
                    break;
                }
            }
            if (isFull) {
                int leastActiveExamInLongTB = 0;
                for (int i = 1; i < longTB.length; i++) {
                    if (mvf.item(longTB[leastActiveExamInLongTB].getExamID()) >= mvf.item(longTB[i].getExamID())) {
                        leastActiveExamInLongTB = i;
                    }
                }
                longTB[leastActiveExamInLongTB] = exam;
            }
        }
    }

    private void updateMVFWithSwap(Hashmap<Integer, Integer> mvf, Swap bestSwap) {
        updateMVFWithExam(mvf, bestSwap.getExam());
        updateMVFWithExam(mvf, bestSwap.getExam2());
    }

    private void updateMVFWithExam(Hashmap<Integer, Integer> mvf, Exam exam) {
        if (mvf.contains(exam.getExamID())) {
            int freq = mvf.item(exam.getExamID());
            mvf.delete(exam.getExamID());
            mvf.add(exam.getExamID(), freq + 1);
        } else {
            mvf.add(exam.getExamID(), 1);
        }
    }

    private void updateShortTBWithSwap(LinkedList<MoveTenure> shortTB, Swap bestSwap, int tenureShort) throws Exception {
        Move move1 = new Move(bestSwap.getExam(), bestSwap.getTimeslotTo(), bestSwap.getRoomTo());
        Move move2 = new Move(bestSwap.getExam2(), bestSwap.getTimeslotFrom(), bestSwap.getRoomFrom());
        updateShortTBWithMove(shortTB, move1, tenureShort);
        updateShortTBWithMove(shortTB, move2, tenureShort);
    }

    private void updateShortTBWithMove(LinkedList<MoveTenure> shortTB, Move bestMove, int tenureShort) throws Exception {
        boolean bestMoveInShortTB = false;
        for (int i = 0; i < shortTB.len(); i++) {
            shortTB.getValue(i).decrementTenure();
            if (moveNotInShortTB(shortTB, bestMove.getExam())) {
                if (shortTB.getValue(i).tenureZero()) {
                    shortTB.remove(shortTB.getValue(i));
                }
            } else {
                bestMoveInShortTB = true;
                shortTB.remove(shortTB.getValue(i));
            }
        }
        if (!bestMoveInShortTB) {
            shortTB.append(new MoveTenure(bestMove, tenureShort));
        }
    }

    private int getMoveCost(Exam[][] currentSol, Move move) throws Exception {
        Exam[][] tempSol = deepDuplicateCurrentSol(currentSol);
        int[] times = getTimesForMove(move);
        int timeFrom = times[0];
        int timeTo = times[1];
        performMove(tempSol, timeFrom, timeTo, move);
        return costFunction(tempSol);
    }

    private int[] getTimesForMove(Move move) {
        int timeFrom = 0;
        int timeTo = 0;
        for (int i = 0; i < TRC.length; i++) {
            if (TRC[i][0].getTimeslot().getWeekNum() == move.getTimeslotFrom().getWeekNum()
                    && TRC[i][0].getTimeslot().getPeriodNum() == move.getTimeslotFrom().getPeriodNum()) {
                timeFrom = i;
            }
            if (TRC[i][0].getTimeslot().getWeekNum() == move.getTimeslotTo().getWeekNum()
                    && TRC[i][0].getTimeslot().getPeriodNum() == move.getTimeslotTo().getPeriodNum()) {
                timeTo = i;
            }
        }
        return new int[]{timeFrom, timeTo};
    }

    private int getSwapCost(Exam[][] currentSol, Swap swap) throws Exception {
        Exam[][] tempSol = deepDuplicateCurrentSol(currentSol);
        int[] times = getTimesForSwap(swap);
        int timeFrom = times[0];
        int timeTo = times[1];
        performSwap(tempSol, swap, timeFrom, timeTo);
        return costFunction(tempSol);
    }

    private int costFunction(Exam[][] solution) {
        System.out.println("cost");
        int weight1 = 8; //biases for cost function. weight having consecutive exams as worse than wasted space
        int weight2 = 3;
        int totalConsec = 0; //number of instances where a student has an exam in back to back periods
        int wastedSpace = 0; //how much space in a room is wasted (placing a small exam in large room increases this)
        for (int i = 0; i < solution.length - 1; i++) {
            LinkedList<Integer> students = new LinkedList<>(); //all students with consecutive exams
            if (solution[i] != null && solution[i + 1] != null && solution[i].length > 0 && solution[i + 1].length > 0 && examsInConsecTimeslots(solution[i][0], solution[i+1][0])) { //if there are exams in timeslots i and i+1
                for (int j = 0; j < solution[i].length; j++) { //totalConsec
                    for (int k = 0; k < solution[i][j].enrolment(); k++) {
                        students.append(solution[i][j].getStudents()[k]);
                    }
                }
                for (int j = 0; j < solution[i + 1].length; j++) {
                    for (int k = 0; k < solution[i + 1][j].enrolment(); k++) {
                        if (students.contains(solution[i + 1][j].getStudents()[k]))
                            totalConsec++;
                    }
                }
            }
        }
        for (Exam[] timeslot : solution) { //wastedSpace
            if (timeslot != null) {
                for (Exam exam : timeslot) {
                    wastedSpace += exam.getRoom().getCapacity() - exam.enrolment();
                }
            }
        }
        System.out.println((weight1 * totalConsec) + (weight2 * wastedSpace));
        return (weight1 * totalConsec) + (weight2 * wastedSpace); //cost
    }

    private boolean examsInConsecTimeslots(Exam exam, Exam exam1) {
        /* iff they are one period apart,
         * on the same week,
         * and on the same day (the earlier exam is not on the last period of a day)
         * */
        return (Math.abs(exam.getPeriodNum() - exam1.getPeriodNum()) == 1
                && exam.getWeekNum() == exam1.getWeekNum()
                && Math.min(exam.getPeriodNum(), exam1.getPeriodNum()) % 7 != 0);
    }

    private boolean swapNotInShortTB(Swap swap, LinkedList<MoveTenure> shortTB) throws Exception {
        return moveNotInShortTB(shortTB, swap.getExam()) && moveNotInShortTB(shortTB, swap.getExam2());
    }

    private boolean moveNotInLongTB(Exam[] longTB, Exam exam1) {
        for (Exam exam : longTB) {
            if (exam == null)
                continue;
            if (exam.getExamID() == exam1.getExamID()) {
                return false;
            }
        }
        return true;
    }

    private boolean moveNotInShortTB(LinkedList<MoveTenure> shortTB, Exam exam1) throws Exception {
        for (int i = 0; i < shortTB.len(); i++) {
            if (shortTB.getValue(i).getExam().getExamID() == exam1.getExamID()
                    && shortTB.getValue(i).getRoomFrom().getRoomID() == exam1.getRoom().getRoomID()
                    && shortTB.getValue(i).getTimeslotFrom().getPeriodNum() == exam1.getPeriodNum()
                    && shortTB.getValue(i).getTimeslotFrom().getWeekNum() == exam1.getWeekNum()) {
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
        ConflictNode[][] tempTRC =  connect.getTRC();
        connect.close();
        return tempTRC;
    }

    private void getExams() throws Exception {
        DatabaseConnect connect = new DatabaseConnect();
        this.exams = connect.getAllExams();
        connect.close();
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
            if (left.getValue(i).enrolment() > right.getValue(j).enrolment()) {
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

    private void sort(Exam[] arr, int start, int end) throws Exception {
        if (start < end) {
            int middle = start + (end - start) / 2;
            sort(arr, start, middle);
            sort(arr, middle + 1, end);
            merge(arr, start, middle, end);
        }
    }
}
