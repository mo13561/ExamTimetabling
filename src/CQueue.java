public class CQueue<T> { // Generic circular queue
    private CQueue<T> front;
    private CQueue<T> rear;
    private T current;
    private CQueue<T> next;
    private int maxSize;
    private int currentSize;

    public CQueue() {
        this.maxSize = 4;
    }

    public CQueue(int maxSize) {
        this.maxSize = maxSize;
    }

    public CQueue(T value, CQueue<T> next) {
        this.current = value;
        this.next = next;
    }

    public void add(T value) throws Exception {
        if (isEmpty()) {
            this.rear = this.front = new CQueue<>(value, next);
        } else if (isFull()) {
            throw new Exception("Queue is full, you cannot add more");
        } else {
            this.rear.next = this.rear = new CQueue<>(value, next);
        }
        this.currentSize++;
    }

    public T remove() throws Exception {
        T removed;
        if (!isEmpty()) {
            removed = this.front.current;
            this.front = this.front.next;
        } else {
            throw new Exception("You cannot remove a value that does not exist!");
        }
        if (this.front == null) {
            this.rear = null;
        }
        this.currentSize--;
        return removed;
    }

    public boolean isEmpty() {
        return this.rear == null;
    }

    public boolean isFull() {
        return this.maxSize == this.currentSize;
    }
}