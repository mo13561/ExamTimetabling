public class PriorityQ <T> {//Generic priority queue with priority as a parameter
    static class Node<T> {//Node class to make linked list
        T value;
        int priority;
        Node<T> next;
        Node<T> before;

        public Node(T value, Node<T> before, int priority) {
            this.value = value;
            this.priority = priority;
            this.before = before;
            this.next = null;
        }

        public Node(T value, Node<T> next, Node<T> before, int priority) {
            this.priority = priority;
            this.value = value;
            this.next = next;
            this.before = before;
        }
    }

    private int size;
    private Node<T> head;//first item in queue

    public PriorityQ() {
        this.head = null;
        this.size = 0;
    }

    public int size() {
        return this.size;
    }

    public void add(T value, int priority) {
        if (isEmpty()) {
            this.head = new Node<>(value, null, priority);
        } else if (this.head.priority > priority) {
            this.head = new Node<>(value, this.head, this.head.before, priority);
        } else {
            Node<T> traversal = this.head;
            while (traversal.next != null && traversal.next.priority <= priority) {
                traversal = traversal.next;
            }
            if (traversal.next == null) {
                traversal.next = new Node<>(value, traversal, priority);
            } else {
                traversal.next = traversal.next.before = new Node<>(value, traversal.next, traversal, priority);
            }
        }
        this.size++;
    }

    public T remove(T value) throws Exception {
        if (isEmpty()) throw new Exception("The list is empty, there is no element to remove");
        Node<T> current = this.head;
        while (current.next != null && !current.value.toString().equals(value.toString())) {
            current = current.next;
        }
        if (!current.value.toString().equals(value.toString())) throw new Exception("There is no value " + value + " in the list");
        if (this.head == current) {
            this.head = this.head.next;
            this.head.before = null;
        } else if (current.next == null) {
            current.before.next = null;
        } else {
            current.before.next = current.next;
            current.next.before = current.before;
        }
        this.size--;
        return current.value;
    }

    public T pop() throws Exception {//removes first item in queue
        if (isEmpty()) {
            throw new Exception("The queue is empty, you cannot remove any items from it.");
        }
        T value = this.head.value;
        if (this.size == 1) {
            this.head = null;
        } else {
            this.head = this.head.next;
            this.head.before = null;
        }
        this.size--;
        return value;
    }

    public T get(int index) throws Exception {
        if (index >= this.size) throw new Exception("Invalid pointer exception");
        Node<T> traversal = this.head;
        for (int i = index; i > 0; i--) {
            traversal = traversal.next;
        }
        return traversal.value;
    }

    public String toString() {//toString method to display queue
        if (isEmpty()) {
            return "";
        }
        String response = "" + this.head.value;//making it look nice
        Node<T> traversal = this.head.next;
        for (int i = 0; i < this.size - 1; i++) {
            response += " <- " + traversal.value;
            if (traversal.next != null) {
                traversal = traversal.next;
            }
        }
        return response;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }
}