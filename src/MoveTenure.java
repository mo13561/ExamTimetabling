public class MoveTenure extends Move {
    private int tenure;
    public MoveTenure(Move move, int tenure) {
        super(move);
        this.tenure = tenure;
    }

    public void decrementTenure() {
        this.tenure--;
    }

    public boolean tenureZero() {
        return this.tenure <= 0;
    }
}
