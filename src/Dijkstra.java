public class Dijkstra {
    private int vertexNum;
    private Hashmap<Integer, Integer> distances; //distances from "source" to each node
    private int[] lastVisited;
    private LinkedList<Integer> visited;
    private PriorityQ<Integer> pq;

    public Dijkstra(AdjList<Integer> adjList, int src) throws Exception {
        vertexNum = adjList.size();
        distances = new Hashmap<>();//vertexnum number.
        visited = new LinkedList<>();
        pq = new PriorityQ<>();
        int maxValue = -1;
        Hashmap.KeyValue[] nodes = adjList.getNodes();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] == null) continue;
            distances.add((int) nodes[i].getUnconvertedKey(), Integer.MAX_VALUE);
            maxValue = Integer.max(maxValue, (Integer) nodes[i].getUnconvertedKey() + 1);
        }
        lastVisited = new int[maxValue];
        lastVisited[src] = src;
        pq.add(src, 0);
        distances.getKeyValue(src).setValue(0);
        while (visited.len() != vertexNum && !pq.isEmpty()) {
            int minNode = pq.pop();
            if (visited.search(minNode))
                continue;
            int newDist;
            Hashmap.KeyValue[] currentNodes = adjList.getAdjacentNodes(minNode).getKeys();
            for (int i = 0; i < currentNodes.length; i++) {
                if (currentNodes[i] == null) continue;
                DNode neighbour = new DNode((int) currentNodes[i].getUnconvertedKey(),(int) currentNodes[i].getValue());
                if (!visited.search(neighbour.nodeName)) {
                    newDist = distances.item(minNode) + neighbour.distFromSrc;
                    if (newDist < distances.item(neighbour.nodeName)) {
                        distances.getKeyValue(neighbour.nodeName).setValue(newDist);
                        lastVisited[neighbour.nodeName] = minNode;
                    }
                    pq.add(neighbour.nodeName, distances.item(neighbour.nodeName));
                }
            }
            visited.append(minNode);
        }
    }

    public int PathLength(int endNode) {
        Hashmap.KeyValue[] nodes = distances.getKeys();
        for (int i = 0 ; i < nodes.length; i++) {
            if (nodes[i] == null) continue;
            System.out.println(nodes[i].getUnconvertedKey() + "|| distance: " + nodes[i].getValue());
        }
        return distances.item(endNode);
    }

    public String Path(int endNode) {
        String ans = "";
        int x = endNode;
        while (lastVisited[x] != x) {
            ans = lastVisited[x] + " -> " + ans;
            x = lastVisited[x];
        }
        return ans + endNode;
    }
}