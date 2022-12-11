public class Maxflow {
    private final int numNodes;
    private int flow;
    private int[][] resGraph;
    private final int source;
    private final int sink;

    public Maxflow(int[][] graph, int source, int sink) {
        this.numNodes = graph.length;
        createResGraph(graph);
        this.source = source;
        this.sink = sink;
        this.flow = 0;
    }

    private void createResGraph(int[][] graph) {
        this.resGraph = new int[graph.length][graph.length];
        for (int i = 0; i < this.resGraph.length; i++) {
            System.arraycopy(graph[i], 0, this.resGraph[i], 0, this.resGraph[i].length);
        }
    }

    public void findFlow() throws Exception {
        int[] parent = new int[this.numNodes];
        while (bfs(parent)) {
            int path = Integer.MAX_VALUE; //set flow through augmenting path as infinity
            for (int node = this.sink; node != this.source; node = parent[node]) {
                path = Math.min(path, this.resGraph[parent[node]][node]);
            }
            for (int node = this.sink; node != this.source; node = parent[node]) {
                this.resGraph[parent[node]][node] -= path; //update residual graph with new flow
                this.resGraph[node][parent[node]] += path;
            }
            this.flow += path; //add augmenting path to flow
        }
    }

    private boolean bfs(int[] parent) throws Exception {
        boolean[] visited = new boolean[this.numNodes]; //set of all nodes in graph and whether they've been visited
        CQueue<Integer> queue = new CQueue<>(this.numNodes);
        queue.add(this.source);
        visited[source] = true;
        parent[this.source] = -1;

        while (!queue.isEmpty()) {
            int node = queue.remove();

            for (int i = 0; i < this.numNodes; i++){
                if (!visited[i] && this.resGraph[node][i] > 0) {
                    parent[i] = node; //set parent of this adjacent node to 'node'
                    if (i == this.sink) { //find sink from node then set node parent and stop bfs
                        return true;
                    }
                    queue.add(i); //add this node to queue
                    visited[i] = true; //now visited this adjacent node
                }
            }
        }
        return false;
    }

    public int getFlow() {
        return flow;
    }

    public int[][] getResGraph() {
        return resGraph;
    }
}
