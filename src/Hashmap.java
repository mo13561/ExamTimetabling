public class Hashmap<K, T> {//generic hashmap modulus 11, dynamic.
    class KeyValue<V> {
        private final int key;
        private int position;
        private V value;
        private final K unconvertedKey;

        public KeyValue(K key, V value) {
            this.key = keyToInt(key);
            this.unconvertedKey = key;
            setValue(value);
            setPosition(hashingFunction(this.key));
        }

        public int hashingFunction(int key) {
            int pos = key % modulus;
            if (map[pos] != null) {
                pos = hashingFunction(key + 1);
            }
            return pos;
        }

        public int getKey() {
            return key;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        public K getUnconvertedKey() {
            return unconvertedKey;
        }
    }
    private int maxSize = 11;
    private final int modulus = 11;
    private int length = 0;
    KeyValue<T>[] map;

    public Hashmap() {
        this.map = new KeyValue[this.maxSize];
    }

    public Hashmap(int size) {
        this.maxSize = size;
        this.map = new KeyValue[this.maxSize];
    }

    public int keyToInt(K key) {
        int numKey = 0;
        if (key instanceof String) {
            for (int i = 0; i < ((String) key).length(); i++) {
                numKey += ((String) key).charAt(i);
            }
        } else if (key instanceof Character) {
            numKey = (int) ((Character) key);
        } else if (key instanceof Double) {
            numKey = ((Double) key).intValue();
        } else if (key instanceof Integer) {
            numKey = (Integer) key;
        }
        return numKey;
    }

    public void add(K key, T value) throws Exception {
        if (isFull()) {
            enlarge();
        }
        if (contains(keyToInt(key))) {
            throw new IllegalArgumentException("The key is already present");
        }
        KeyValue<T> helper = new KeyValue<>(key, value);
        map[helper.position] = helper;
        this.length++;
    }

    private void enlarge() {
        this.maxSize *= 2;
        KeyValue<T>[] temp = map;
        map = new KeyValue[this.maxSize];
        for (int i = 0; i < temp.length; i++) {
            map[i] = temp[i];
        }
    }

    public void delete(K key) throws IllegalArgumentException {
        if (!contains(key)) {
            throw new IllegalArgumentException("The given key does not exist.");
        }
        map[posFind(key)] = null;
        this.length--;
        this.rearrange();
    }

    public void rearrange() {
        for (int i = 0; i < this.maxSize; i++) {
            if (map[i] == null) {
                continue;
            }
            int prefPos = map[i].key % this.modulus;
            while (prefPos != i) {
                if (map[prefPos] == null) {
                    map[prefPos] = map[i];
                    map[i] = null;
                    map[prefPos].setPosition(prefPos);
                    continue;
                }
                prefPos = ++prefPos % this.modulus;
            }
        }
    }

    public T item(int key) throws IllegalArgumentException {
        if (!contains(key)) {
            throw new IllegalArgumentException("The given key does not exist.");
        }
        return map[posFind(key)].getValue();
    }

    public T item(K key) throws IllegalArgumentException {
        if (!contains(key)) {
            throw new IllegalArgumentException("The given key does not exist.");
        }
        return map[posFind(key)].getValue();
    }
    public KeyValue<T> getKeyValue(int key) throws IllegalArgumentException {
        if (!contains(key)) {
            throw new IllegalArgumentException("The given key does not exist.");
        }
        return map[posFind(key)];
    }

    public int[] getNumericKeys() {
        int[] allKeys = new int[this.length];
        int index = 0;
        for (int i = 0; i < this.maxSize; i++) {
            if (map[i] == null) {
                continue;
            }
            allKeys[index++] = map[i].getKey();
        }
        return allKeys;
    }

    public KeyValue<T>[] getKeys() {
        return map;
    }

    public int posFind(int key) {
        int pos = key % this.modulus;
        for (int i = 0; i < this.maxSize; i++) {
            if (map[pos] == null) {
                continue;
            }
            if (map[pos].key == key) {
                return pos;
            }
            pos = ++pos % this.modulus;
        }
        return -1;
    }

    public int posFind(K genKey) {
        int key = keyToInt(genKey);
        int pos = key % this.modulus;
        for (int i = 0; i < this.maxSize; i++) {
            if (map[pos] == null) {
                continue;
            }
            if (map[pos].key == key && genKey.toString().equals(map[pos].getUnconvertedKey().toString())) {
                return pos;
            }
            pos = ++pos % this.modulus;
        }
        return -1;
    }

    public boolean contains(K genKey) {
        int key = keyToInt(genKey);
        int pos = key % this.modulus;
        for (int i = 0; i < this.maxSize; i++) {
            if (map[pos] == null) continue;
            if (map[pos].getKey() == key && map[pos].getUnconvertedKey().toString().equals(genKey.toString())) {
                return true;
            }
            pos = ++pos % this.modulus;
        }
        return false;
    }

    public boolean contains(int key) {
        int pos = key % this.modulus;
        for (int i = 0; i < this.maxSize; i++) {
            if (map[pos] == null) {
                continue;
            }
            if (map[pos].getKey() == key) {
                return true;
            }
            pos = ++pos % this.modulus;
        }
        return false;
    }

    public String toString() {
        String display = "";
        for (int i = 0; i < this.maxSize; i++) {
            if (map[i] == null) {
                continue;
            }
            display += "[ " + map[i].getUnconvertedKey() + " : " + map[i].getValue() + " ]";
        }
        return display;
    }

    public boolean isFull() {
        return this.length() == this.maxSize;
    }

    public boolean isEmpty() {
        return this.length() == 0;
    }

    public int length() {
        return this.length;
    }
}
