package agilexs.catalogxsadmin.presentation.client.util;

import java.util.Map;

public class Entry<K,V> implements Map.Entry<K,V> {

  private final K key;
  private V value;

  public Entry(K key) {
    this.key = key;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Entry ? ((Entry<K,V>)obj).getKey().equals(getKey()) :
      false; 
  }

  @Override
  public int hashCode() {
    return getKey().hashCode();
  }

  @Override public K getKey() {
    return key;
  }

  @Override public V getValue() {
    return value;
  }

  @Override public V setValue(V value) {
    return this.value = value;
  }
}
