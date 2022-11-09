public class LinkedList<G> {
    class Node {
        G value;
        Node next;
        Node previous;

        public Node(G value) {
            this.value = value;
        }

        public Node(G value, Node previous, Node next) {
            this.value = value;
            this.previous = previous;
            this.next = next;
        }

        public G getValue() {
            return this.value;
        }

        public String toString() {
            return this.value + "";
        }

        public boolean hasNext() {
            return this.next != null;
        }

        public Node getNext() {
            return next;
        }

        public void setNext(Node next) {
            this.next = next;
        }

    }

    Node front;
    Node rear;
    private int length;

    public LinkedList() {
        this.front = this.rear =  null;
        this.length = 0;
    }

    public void append(G value) {
        if (isEmpty()) {
            this.front = this.rear = new Node(value);
        } else {
            this.rear.next = new Node(value, this.rear, null);
            this.rear = this.rear.next;
        }
        this.length++;
    }

    public G remove(G value) throws Exception {
        if (isEmpty()) throw new Exception("The list is empty, there is no element to remove");
        Node current = this.front;
        while (current.next != null && !current.getValue().toString().equals(value.toString())) {
            current = current.next;
        }
        if (!current.getValue().toString().equals(value.toString())) throw new Exception("There is no value " + value + " in the list");
        if (current.previous == null) {
            if (len() == 1) {
                this.front = null;
            } else {
                this.front = this.front.next;
                this.front.previous = null;
            }
        } else if (current.next == null) {
            this.rear = this.rear.previous;
            this.rear.next = null;
        } else {
            current.previous.next = current.next;
            current.next.previous = current.previous;
        }
        this.length--;
        return current.getValue();
    }

    public Node getFront() {
        return this.front;
    }

    public int count(G value) {
        int occurrences = 0;
        if (isEmpty()) return occurrences;
        Node current = this.front;
        while (current.next != null) {
            if (current.getValue() == value) occurrences++;
            current = current.next;
        }
        return occurrences;
    }

    public int len() {
        return this.length;
    }

    public int index(G value) throws Exception {
        if (value == this.front.getValue()) return 0;
        Node current = this.front;
        int valueIndex = 0;
        while (current.next != null && !current.getValue().toString().equals(value.toString())) {
            current = current.next;
            valueIndex++;
        }
        if (!current.getValue().toString().equals(value.toString())) throw new Exception("There is no such value present in the list");
        return valueIndex;
    }

    public void insert(int pos, G value) throws Exception {
        if (pos >= this.length || pos < 0) throw new Exception("That index is out of the range of the list");
        if (pos == 0) {
            this.front.previous = new Node(value, null, this.front);
            this.front = this.front.previous;
        } else {
            Node current = this.front;
            for (int i = 0; i < pos; i++) {
                current = current.next;
            }
            current.previous = new Node(value, current.previous, current);
            current.previous.previous.next = current.previous;
        }
        length++;
    }

    public G pop() throws UnsupportedOperationException {
        if (isEmpty()) throw new UnsupportedOperationException("The list is empty");
        Node removed = this.front;
        this.front = this.front.next;
        this.front.previous = null;
        this.length--;
        return removed.getValue();
    }

    public G popLast() throws Exception {
        if (isEmpty()) throw new Exception("The list is empty");
        Node removed = this.rear;
        this.rear = this.rear.previous;
        this.rear.next = null;
        this.length--;
        return removed.getValue();
    }

    public G pop(int index) throws Exception {
        if (isEmpty()) throw new Exception("The list is empty");
        if (index >= length || index < 0) throw new Exception("The specified index to out of bounds");
        Node removed = index == this.length - 1 ? this.rear : this.front;
        if (index == 0) {
            this.front = this.front.next;
            this.front.previous = null;
        } else if (index == length - 1) {
            this.rear = this.rear.previous;
            this.rear.next = null;
        } else {
            for (int i = 0; i < index; i++) {
                removed = removed.next;
            }
            removed.previous.next = removed.next;
            removed.next.previous = removed.previous;
        }
        this.length--;
        return removed.getValue();
    }

    public boolean search(G value) {
        if (this.len() == 0) {
            return false;
        }
        if (this.front.getValue().toString().equals(value.toString()) || this.rear.getValue().toString().equals(value.toString())) {
            return true;
        } else {
            return this.len() > 1 ? contains(value, this.front.next) : false;
        }
    }

    private boolean contains(G value, Node traversal) {
        if (traversal.getValue() == value) {
            return true;
        } else if (!traversal.hasNext()) {
            return false;
        }
        return contains(value, traversal.next);
    }

    public G getValue(int index) throws Exception {
        if (isEmpty()) throw new Exception("The list is empty");
        if (index >= length || index < 0) throw new Exception("The specified index to out of bounds");
        Node node = this.front;
        for (int i = 0; i < index; i++) {
            node = node.next;
        }
        return node.getValue();
    }

    public boolean isEmpty() {
        return this.front == null;
    }

    public String toString() {//toString method to display queue
        if (isEmpty()) return "";
        String list = "" + this.front.getValue();//making it look nice
        Node traversal = this.front.next;
        for (int i = 0; i < this.length - 1; i++) {
            list += " <- " + traversal.getValue();
            if (traversal.next != null) {
                traversal = traversal.next;
            }
        }
        return list;
    }
}