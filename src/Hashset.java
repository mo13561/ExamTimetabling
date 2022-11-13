public class Hashset<G> extends Hashmap<G,G> {

    public Hashset() {
        super();
    }

    public void add(G value) {
        if (!super.contains(value)) {
            super.add(value, value);
        }
    }
}
