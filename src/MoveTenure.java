public class MoveTenure {
    private Move move;
    private int tenure;
    public MoveTenure(Move move, int tenure) {
        this.move = move;
        this.tenure = tenure;
    }

    public int getTenure() {
        return tenure;
    }

    public void decrementTenure() {
        this.tenure--;
    }

    public boolean tenureZero() {
        return this.tenure <= 0;
    }

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }
}
