public class AdjList<G> {//generic adjacency list made with generic Hashmap
    private final Hashmap<G, Hashmap<G, Integer>> adjList;
    private boolean directed = false;
    public AdjList() {
        adjList = new Hashmap<>();
    }

    public AdjList(boolean directed) {
        this.directed = directed;
        adjList = new Hashmap<>();
    }

    public void add(G src, G dest, int weight) throws Exception {
        boolean contsrc = true;
        boolean contdest = true;
        if (!adjList.contains(src)) {
            Hashmap<G, Integer> temp = new Hashmap<>();
            temp.add(dest, weight);
            adjList.add(src, temp);
            contsrc = false;
        }
        if (!adjList.contains(dest) && !directed) {
            Hashmap<G, Integer> temp = new Hashmap<>();
            temp.add(src, weight);
            adjList.add(dest, temp);
            contdest = false;
        }
        if (contsrc) {
            if (adjList.item(src).contains(dest)) {
                throw new Exception("The edge already exists");
            }
            adjList.item(src).add(dest, weight);
        }
        if (contdest && !directed) {
            if (adjList.item(dest).contains(src)) {
                throw new Exception("The edge already exists");
            }
            adjList.item(dest).add(src, weight);
        }
    }

    public int size() {
        return adjList.length();
    }

    public Hashmap<G, Integer> getAdjacentNodes(G node) {
        return adjList.item(node);
    }

    public int getNumberOfAdjNodes(G node) {
        return adjList.item(node).length();
    }

    public Hashmap.KeyValue[] getNodes() {
        return adjList.getKeys();
    }

    public LinkedList<DNode> getLinkedListOfNodes(G node) {
        LinkedList<DNode> neighbours = new LinkedList<>();
        Hashmap.KeyValue[] nodes = adjList.item(node).getKeys();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] == null) continue;
            neighbours.append(new DNode((int) nodes[i].getUnconvertedKey(),(int) nodes[i].getValue()));
        }
        return neighbours;
    }

    public LinkedList<LinkedList<DNode>> getLinkedAdjList() {
        LinkedList<LinkedList<DNode>> linkedAdj = new LinkedList<LinkedList<DNode>>();
        Hashmap.KeyValue[] nodes = getNodes();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] == null) continue;
            linkedAdj.append(getLinkedListOfNodes((G) nodes[i].getUnconvertedKey()));
        }
        return linkedAdj;
    }

    public boolean contains(G node) {
        return adjList.contains(node);
    }

    public void removeNode(G node) {
        if (directed) {
            int[] connections = adjList.getNumericKeys();
            for (int i = 0; i < connections.length; i++) {
                if (adjList.item(connections[i]).contains(node)) {
                    adjList.item(connections[i]).delete(node);
                }
            }
            adjList.delete(node);
        } else {
            int[] connections = adjList.item(node).getNumericKeys();
            adjList.delete(node);
            for (int i = 0; i < connections.length; i++) {
                adjList.item(connections[i]).delete(node);
            }
        }
    }

    public String toString() {
        return adjList.toString();
    }
}