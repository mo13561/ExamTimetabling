public class ConstructInvigilatorTimetable { //takes in exams that are assigned a timeslot and assigns invigilators to them.
    private Invigilator[] invigilators; // graph[x + this.timeslots.len() + 1][numNodes - 1] is connection between invigilators[x] and sink node
    private final Exam[][] exams;
    private int numExams;
    private LinkedList<Pair<Integer, Integer>> timeslots; //(index in residual graph,index in timetable array) index + 1 is the index (0 indexing) in graph.

    public ConstructInvigilatorTimetable(Exam[][] exams) {
        this.exams = exams;
        getInvigilators();
        setTimeslots();
    }

    private void getInvigilators() {
        DatabaseConnect connect = new DatabaseConnect();
        try {
            this.invigilators = connect.getAllInvigilators();
        } catch (Exception e) {
            e.printStackTrace();
        }
        connect.close();
    }

    private void setTimeslots() {
        this.timeslots = new LinkedList<>();
        int counter = 1;
        for (int i = 0; i < this.exams.length; i++) {
            if (this.exams[i] == null || this.exams[i].length == 0)
                continue;
            this.timeslots.append(new Pair<>(counter, i));
            counter++;
        }
    }

    public Exam[][] addInvigilators() throws Exception {
        int[][] invGraph = getInvigilatorGraph();
        Maxflow maxflow = new Maxflow(invGraph, 0, invGraph.length - 1);
        maxflow.findFlow();
        if (maxflow.getFlow() < this.numExams) {
            System.out.println("The invigilators provided are unable to invigilate all the exams");
            return null;
        } else {
            int[][] resGraph = maxflow.getResGraph();
            for (int i = 1; i < this.timeslots.len() + 1; i++) {
                for (int j = this.timeslots.len() + 1; j < this.timeslots.len() + this.invigilators.length + 1; j++) { //every invigilator
                    int timeslotIndex = getTimetableIndex(i);
                    if (resGraph[i][j] == 0) { //if there was a flow between timeslot and invigilator, then assign invigilator to an exam in that timeslot
                        for (int k = 0; k < this.exams[timeslotIndex].length; k++) {
                            if (this.exams[timeslotIndex][k].getInvigilator() == null) { //first exam in timeslot that is not assigned an invigilator
                                this.exams[timeslotIndex][k].setInvigilator(invigilators[j - this.timeslots.len() - 1]);
                                resGraph[i][j] = 1;
                                break;
                            }
                        }
                    }
                }
            }
        }
            return this.exams;
    }

    private int getTimetableIndex(int resIndex) throws Exception {
        for (int i = 0; i < this.timeslots.len(); i++) {
            if (this.timeslots.getValue(i).getFirst() == resIndex) {
                return this.timeslots.getValue(i).getSecond();
            }
        }
        return -1;
    }

    private int[][] getInvigilatorGraph() throws Exception {
        int numNodes = this.timeslots.len() + this.invigilators.length + 2;
        int[][] graph = new int[numNodes][numNodes]; //first is source, 1 to no. timeslots is timeslots, no. timeslots + 1 to (no. timeslots + 1 + no. invigilators) are invigilators
        for (int i = 1; i < this.timeslots.len() + 1; i++) { //all timeslots.
            graph[0][i] = this.exams[timeslots.getValue(i - 1).getSecond()].length; //adding no. exams per timeslots for edges from source to timeslot;
            this.numExams += graph[0][i];
            for (int j = this.timeslots.len() + 1; j < numNodes - 1; j++) { //every invigilator
                graph[i][j] = 1;
            }
        }
        for (int j = this.timeslots.len() + 1; j < numNodes - 1; j++) { //every invigilator
            graph[j][numNodes - 1] = invigilators[j - this.timeslots.len() - 1].getExamsLeft(); // graph[x + this.timeslots.len() + 1][numNodes - 1] is connection between invigilators[x] and sink node
        }
        return graph;
    }
}
