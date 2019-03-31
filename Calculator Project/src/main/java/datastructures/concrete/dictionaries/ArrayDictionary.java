package datastructures.concrete.dictionaries;

import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;

/**
 * See IDictionary for more details on what this class should do
 */
public class ArrayDictionary<K, V> implements IDictionary<K, V> {
    // You may not change or rename this field: we will be inspecting
    // it using our private tests.
    private Pair<K, V>[] pairs;
    private int size;

    // You're encouraged to add extra fields (and helper methods) though!

    public ArrayDictionary() {
        this.pairs = makeArrayOfPairs(10);
        this.size = 0;
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain Pair<K, V> objects.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private Pair<K, V>[] makeArrayOfPairs(int arraySize) {
        // It turns out that creating arrays of generic objects in Java
        // is complicated due to something known as 'type erasure'.
        //
        // We've given you this helper method to help simplify this part of
        // your assignment. Use this helper method as appropriate when
        // implementing the rest of this class.
        //
        // You are not required to understand how this method works, what
        // type erasure is, or how arrays and generics interact. Do not
        // modify this method in any way.
        return (Pair<K, V>[]) (new Pair[arraySize]);
    }
    
    public V get(K key) {
        if (!this.containsKey(key)) {
            throw new NoSuchKeyException();
        }
        return this.pairs[keyIndex(key)].value;
    }

    public void put(K key, V value) {
        if (this.size >= this.pairs.length) {
            Pair<K, V>[] newPairs = this.makeArrayOfPairs(2 * this.size);
            for (int i = 0; i < this.size; i++) {
                newPairs[i] = this.pairs[i];
            }
            this.pairs = newPairs;
        }
        if (this.containsKey(key)) {
            this.pairs[keyIndex(key)].value = value;
        } else {
            this.pairs[size] =  new Pair<K, V>(key, value);
            this.size++;
        }   
    }

    public V remove(K key) {
        V remove = null;
        if (!this.containsKey(key)) {
            throw new NoSuchKeyException();
        }
        int index = keyIndex(key);
        remove = this.pairs[index].value;
        this.pairs[index] = null;
        this.pairs[index] = this.pairs[this.size - 1];
        this.pairs[this.size - 1] = null;
        this.size--;
        return remove;
    }

    public boolean containsKey(K key) {
        return keyIndex(key) != -1;
    }
    
    public int size() {
        return this.size;
    }
    
    private int keyIndex(K key) {
        for (int i = 0; i < this.size; i++) {
            if (equalKey(i, key)) {
                return i;
            }
        }
        return -1;
    }
    
    private boolean equalKey(int i, K key) {
        return (key == null && this.pairs[i].key == null) || this.pairs[i].key.equals(key);
    }

    private static class Pair<K, V> {
        public K key;
        public V value;

        // You may add constructors and methods to this class as necessary.
        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public String toString() {
            return this.key + "=" + this.value;
        }
    }
}
