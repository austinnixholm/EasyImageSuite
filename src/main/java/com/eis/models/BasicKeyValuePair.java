package com.eis.models;

/**
 * Basic implementation of a Key/Value pair.
 * @param <K> the generic key
 * @param <V> the generic value
 *
 * @author Austin Nixholm
 */
public class BasicKeyValuePair<K, V>{
    K key;
    V value;
    public BasicKeyValuePair() { }
    public BasicKeyValuePair(K key, V  value) {
        this.key = key;
        this.value = value;
    }
    public void setValue(V value) {
        this.value = value;
    }
    public V getValue() {
        return value;
    }
    public void setKey(K key) {
        this.key = key;
    }
    public K getKey() {
        return key;
    }
}
