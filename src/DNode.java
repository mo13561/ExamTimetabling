import java.util.Comparator;

public class DNode implements Comparator<DNode> {
    public int nodeName;
    public int distFromSrc;
    public DNode(int nodeName, int distFromSrc) {
        this.nodeName = nodeName;
        this.distFromSrc = distFromSrc;
    }

    @Override
    public int compare(DNode first, DNode second) {
        return Integer.compare(first.distFromSrc, second.distFromSrc);
    }
}