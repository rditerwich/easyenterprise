package easyenterprise.lib.util;

import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Maps.immutableEntry;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Objects;

/**
 * Simple, Small, Static key-value map-like class. It's optimized for 
 * memory usage (and transport size), and is tailored for the 'simple'
 * cases: when there is only one item, all items have null keys, all 
 * items have the same key, or when there is only one value per key.
 * Large content is inefficient, specially when building them. Class
 * is particularly useful for caching and transport purposes.   
 * 
 * All keys and values remain ordered according to order of insertion.
 */
public abstract class SMap<K, V> implements Iterable<Entry<K, V>>, Serializable {

	private static final long serialVersionUID = 1L;
	private static SMap<?, ?> emptyMap = new Empty<Object, Object>(); 
	
	@SuppressWarnings("unchecked")
	public static <K, V> SMap<K, V> empty() {
		return (SMap<K, V>) emptyMap;
	}
	
	/**
	 * When the map is empty there are no keys and no values.
	 */
	public boolean isEmpty() {
		return getKeys().isEmpty();
	}
	
	/**
	 * Returns the first key. Note that null is returned either
	 * when the first key is null or when the map is empty.
	 * Use {@link #getKeys()} to make the distinction. 
	 * @return Key or null.
	 */
	public abstract K getFirstKey();

	/**
	 * Returns the keys for which the map has values. There
	 * will be at least one value for these keys, albeit the
	 * null value.
	 * @return Keys or the empty list.
	 */
	public abstract List<K> getKeys();

	/**
	 * Returns the first value of the first key. Note the null is
	 * returned either when the first value of the first key is null
	 * or when the map is empty. Use {@link #getFirstValues()} to make
	 * the distiction.
	 * @return Value or null.
	 */
	public abstract V getFirstValue();
	
	/**
	 * Returns the values corresponding to the first key. The empty
	 * list will be returned only when the map is empty.
	 * @return Values or the empty list.
	 */
	public abstract List<V> getFirstValues();
	
	/**
	 * Returns the value corresponding to the null key.
	 * @return Value or null.
	 */
	public V getValue() {
		return getValue(null);
	}
	
	/**
	 * Returns the first value for given key. Note the null is
	 * returned either when the first value for given key is null
	 * or when there are no values for given key. 
	 * Use {@link #getValues(Object)} to make the distinction.
	 * @param key
	 * @return Value or null.
	 */
	public abstract V getValue(K key);
	
	/**
	 * Returns the values corresponding to the null key.
	 * @return Values or the empty list
	 */
	public List<V> getValues() {
		return getValues(null);
	}
	
	/**
	 * Returns the values for given key. 
	 * @param key
	 * @return Values or the empty list.
	 */
	public abstract List<V> getValues(K key);
	
	/**
	 * Synonym to add(null, value)
	 */
	public SMap<K, V> add(V value) {
		return add(null, value);
	}

	/**
	 * Adds a single key/value to the map
	 * @param key
	 * @param value
	 * @return
	 */
	public abstract SMap<K, V> add(K key, V value);
	
	/**
	 * Synonym to addAll(null, values)
	 */
	public SMap<K, V> addAll(Collection<V> values) {
		return addAll(null, values);
	}
	
	/**
	 * Adds multiple values for given key.
	 * @param key
	 * @param value
	 * @return
	 */
	public final SMap<K, V> addAll(K key, Collection<? extends V> values) {
		if (values.isEmpty()) return this;
		if (values.size() == 1) return add(key, values.iterator().next());
		return doAddAll(key, values);
	}
	
	public SMap<K, V> addAll(Map<K, V> values) {
		SMap<K, V> map = this;
		for (Entry<K, V> entry : values.entrySet()) {
			map = map.add(entry.getKey(), entry.getValue());
		}
		return map;
	}

	/**
	 * It's guaranteed that values.size() > 1 
	 */
	protected abstract SMap<K, V> doAddAll(K key, Collection<? extends V> values);

	<T> T[] concat(T[] values, T value) {
		T[] copy = Arrays.copyOf(values, values.length + 1);
		copy[values.length] = value;
		return copy;
	}
	
	<T> Object[] concat(T value, Collection<? extends T> values2) {
		Object[] result = new Object[1 + values2.size()];
		int i = 0;
		result[i++] = value;
		for (T value2 : values2) {
			result[i++] = value2;
		}
		return result;
	}
	
	<T> T[] concat(T[] values, Collection<? extends T> values2) {
		T[] copy = Arrays.copyOf(values, values.length + values2.size());
		int i = values.length;
		for (T value2 : values) {
			copy[i++] = value2;
		}
		return copy;
	}
}

class Empty<K, V> extends SMap<K, V> {
	private static final long serialVersionUID = 1L;
	public K getFirstKey() {
	  return null;
	}
	public List<K> getKeys() {
		return emptyList();
	}
	public V getFirstValue() {
		return null;
	}
	public V getValue(K key) {
		return null;
	};
	@Override
	public List<V> getFirstValues() {
		return emptyList();
	}
	public List<V> getValues(K key) {
		return emptyList();
	}
	public Iterator<Entry<K, V>> iterator() {
	  return Collections.<Entry<K, V>>emptyList().iterator();
	}
	public SMap<K, V> add(K key, V value) {
		if (key == null) return new NoKeySingleValue<K, V>(value);
		else return new SingleKeySingleValue<K, V>(key, value); 
	}
	public SMap<K,V> doAddAll(K key, Collection<? extends V> values) {
		if (key == null) return new NoKeyMultiValue<K, V>(values.toArray());
		else return new SingleKeyMultiValue<K, V>(key, values.toArray());
	};
}

class NoKeySingleValue<K, V> extends SMap<K, V> {
	private static final long serialVersionUID = 1L;
	private static final List<?> nullList = singletonList(null);
	private final V value;
	public NoKeySingleValue(V value) {
		this.value = value;
	}
	public K getFirstKey() {
	  return null;
	}
	@SuppressWarnings("unchecked")
  public List<K> getKeys() {
		return (List<K>) nullList;
	}
	public V getFirstValue() {
		return value;
	}
	public V getValue(K key) {
		return key == null ? value : null;
	}
	public List<V> getFirstValues() {
	  return singletonList(value);
	}
	public List<V> getValues(K key) {
		if (key != null) return emptyList();
		return singletonList(value);
	}
	public Iterator<Entry<K, V>> iterator() {
	  return singleton(immutableEntry((K) null, value)).iterator();
	}
	public SMap<K, V> add(K key, V value) {
		if (key == null) return new NoKeyMultiValue<K, V>(this.value, value); 
		else return new MultiKeySingleValue<K, V>(concat(null, key), new Object[] { this.value, value }); 
	}
	public SMap<K,V> doAddAll(K key, Collection<? extends V> values) {
		if (key == null) return new NoKeyMultiValue<K, V>(null, this.value, value); 
		else return new MultiKeySingleValue<K, V>(concat(null, key), new Object[] { this.value, value }); 
	};
}

class SingleKeySingleValue<K, V> extends SMap<K, V> {
	private static final long serialVersionUID = 1L;
	private final K key;
	private final V value;
	public SingleKeySingleValue(K key, V value) {
		this.key = key;
		this.value = value;
	}
	public K getFirstKey() {
	  return key;
	}
	public List<K> getKeys() {
		return singletonList(key);
	}
	public V getFirstValue() {
	  return value;
	}
	public V getValue(K key) {
		if (equal(this.key, key)) return value;
		else return null;
	}
	public List<V> getFirstValues() {
		return singletonList(value);
	}
	public List<V> getValues(K key) {
		if (equal(this.key, key)) return singletonList(value);
		else return emptyList();
	}
	public Iterator<Entry<K, V>> iterator() {
	  return singleton(immutableEntry(key, value)).iterator();
	}
	public SMap<K, V> add(K key, V value) {
		if (equal(this.key, key)) return new SingleKeyMultiValue<K, V>(key, this.value, value); 
		else return new MultiKeySingleValue<K, V>(new Object[] { this.key, key }, new Object[] { this.value, value });
	}
	public SMap<K, V> doAddAll(K key, Collection<? extends V> values) {
		if (equal(this.key, key)) return new SingleKeyMultiValue<K, V>(key, concat(this.value, values)); 
		else return new MultiKeyMultiValue<K, V>(new Object[] { this.key, key }, new Object[][] { new Object[] { this.value }, values.toArray() });
	}
}

class MultiKeySingleValue<K, V> extends SMap<K, V> {
	private static final long serialVersionUID = 1L;
	private final Object[] keys;
	private final Object[] values;
	MultiKeySingleValue(Object[] keys, Object[] values) {
		this.keys = keys;
		this.values = values;
	}
	@SuppressWarnings("unchecked")
	public K getFirstKey() {
	  return (K) keys[0];
	}
	@SuppressWarnings("unchecked")
  public List<K> getKeys() {
		return (List<K>) asList(keys);
	}
	@SuppressWarnings("unchecked")
  public V getFirstValue() {
	  return (V) values[0];
	}
	@SuppressWarnings("unchecked")
  public V getValue(K key) {
		for (int i = 0; i < keys.length; i++) {
			if (Objects.equal(keys[i], key)) {
				return (V) values[i];
			}
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public List<V> getFirstValues() {
		return (List<V>) asList(values[0]);
	}
	@SuppressWarnings("unchecked")
	public List<V> getValues(K key) {
		for (int i = 0; i < keys.length; i++) {
			if (equal(keys[i], key)) {
				return (List<V>) asList(values[i]);
			}
		}
		return emptyList();
	}
	public Iterator<Entry<K, V>> iterator() {
		return new Iterator<Map.Entry<K,V>>() {
			private int index = 0;
      public boolean hasNext() {
	      return index < values.length;
      }
      @SuppressWarnings("unchecked")
      public Entry<K, V> next() {
	      return immutableEntry((K) keys[index], (V) values[index++]);
      }
      public void remove() {
      	throw new UnsupportedOperationException();
      }
		};
	}
	public SMap<K, V> add(K key, V value) {
		for (int i = 0; i < keys.length; i++) {
			if (equal(keys[i], key))
				return new MultiKeyMultiValue<K, V>(keys, wrapArray(i, new Object[] { values[i], value } ));
		}
		return new MultiKeySingleValue<K, V>(concat(keys, key), concat(values, value));
	}
	
	public SMap<K, V> doAddAll(K key, Collection<? extends V> values) {
		for (int i = 0; i < keys.length; i++) {
			if (equal(keys[i], key))
				return new MultiKeyMultiValue<K, V>(keys, wrapArray(i, concat(this.values[i], values)));
		}
		return new MultiKeySingleValue<K, V>(concat(keys, key), concat(this.values, values));
	}
	
	private Object[][] wrapArray(int index, Object[] arrayAtIndex) {
		Object[][] valueArrays = new Object[keys.length][];
		for (int i = 0; i < keys.length; i++) {
			valueArrays[i] = index == i ? arrayAtIndex : new Object[] { values[i] };
		}
		return valueArrays;
	}
}

class NoKeyMultiValue<K, V> extends SMap<K, V> {
	private static final long serialVersionUID = 1L;
	private static List<?> nullList = singletonList(null);
	private final Object[] values;
	public NoKeyMultiValue(Object... values) {
		this.values = values;
	}
	public K getFirstKey() {
	  return null;
	}
	@SuppressWarnings("unchecked")
  public List<K> getKeys() {
		return (List<K>) nullList;
	}
	@SuppressWarnings("unchecked")
  public V getFirstValue() {
	  return (V) values[0];
	}
	@SuppressWarnings("unchecked")
  public V getValue(K key) {
		return (V) (key == null ? values[0] : null);
	};
	@SuppressWarnings("unchecked")
	public List<V> getFirstValues() {
		return (List<V>) asList(values);
	}
	@SuppressWarnings("unchecked")
	public List<V> getValues(K key) {
		if (key != null) return emptyList();
		return (List<V>) asList(values);
	}
	public Iterator<Entry<K, V>> iterator() {
		return new Iterator<Map.Entry<K,V>>() {
			private int index = 0;
      public boolean hasNext() {
	      return index < values.length;
      }
      @SuppressWarnings("unchecked")
      public Entry<K, V> next() {
	      return immutableEntry((K) null, (V) values[index++]);
      }
      public void remove() {
      	throw new UnsupportedOperationException();
      }
		};
	}
	public SMap<K, V> add(K key, V value) {
		if (key == null)
			return new NoKeyMultiValue<K, V>(concat(values, value));
		else
			return new MultiKeyMultiValue<K, V>(new Object[] { null, key }, new Object[][] { values, new Object[] { value } }); 
	}
	protected SMap<K,V> doAddAll(K key, Collection<? extends V> values) {
		if (key == null)
			return new NoKeyMultiValue<K, V>(key, concat(this.values, values));
		else
			return new MultiKeyMultiValue<K, V>(new Object[] { null, key }, new Object[][] { this.values, values.toArray() }); 
	}
}

class SingleKeyMultiValue<K, V> extends SMap<K, V> {
	private static final long serialVersionUID = 1L;
	private final K key;
	private final Object[] values;
	public SingleKeyMultiValue(K key, Object... values) {
		this.key = key;
		this.values = values;
	}
	public K getFirstKey() {
		return key;
	}
	public List<K> getKeys() {
		return singletonList(key);
	}
	@SuppressWarnings("unchecked")
	public V getFirstValue() {
		return (V) values[0];
	}
	@SuppressWarnings("unchecked")
	public V getValue(K key) {
		return (V) (key == null ? values[0] : null);
	};
	@SuppressWarnings("unchecked")
	public List<V> getFirstValues() {
		return (List<V>) asList(values);
	}
	@SuppressWarnings("unchecked")
	public List<V> getValues(K key) {
		if (!equal(this.key, key)) return emptyList();
		return (List<V>) asList(values);
	}
	public Iterator<Entry<K, V>> iterator() {
		return new Iterator<Map.Entry<K,V>>() {
			private int index = 0;
      public boolean hasNext() {
	      return index < values.length;
      }
      @SuppressWarnings("unchecked")
      public Entry<K, V> next() {
	      return immutableEntry(key, (V) values[index++]);
      }
      public void remove() {
      	throw new UnsupportedOperationException();
      }
		};
	}
	public SMap<K, V> add(K key, V value) {
		if (equal(this.key, key))
			return new SingleKeyMultiValue<K, V>(key, concat(values, value));
		else
			return new MultiKeyMultiValue<K, V>(new Object[] { this.key, key }, new Object[][] { values, new Object[] { value } }); 
	}
	protected SMap<K,V> doAddAll(K key, Collection<? extends V> values) {
		if (equal(this.key, key))
			return new SingleKeyMultiValue<K, V>(key, concat(this.values, values));
		else
			return new MultiKeyMultiValue<K, V>(new Object[] { this.key, key }, new Object[][] { this.values, values.toArray() }); 
	}
}

class MultiKeyMultiValue<K, V> extends SMap<K, V> {
	private static final long serialVersionUID = 1L;
	private final Object[] keys;
	private final Object[][] values;
	public MultiKeyMultiValue(Object[] keys, Object[][] values) {
		this.keys = keys;
		this.values = values;
	}
	@SuppressWarnings("unchecked")
	public K getFirstKey() {
	  return (K) keys[0];
	}
	@SuppressWarnings("unchecked")
	public List<K> getKeys() {
		return (List<K>) asList(keys);
	}
	@SuppressWarnings("unchecked")
  public V getFirstValue() {
	  return (V) values[0][0];
	}
	@SuppressWarnings("unchecked")
  public V getValue(K key) {
		for (int i = 0; i < keys.length; i++) {
			if (equal(keys[i], key))
				return (V) values[i];
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public List<V> getFirstValues() {
		return (List<V>) asList(values[0]);
	}
	@SuppressWarnings("unchecked")
	public List<V> getValues(K key) {
		for (int i = 0; i < keys.length; i++) {
			if (equal(keys[i], key))
				return (List<V>) asList(values[i]);
		}
		return emptyList();
	}
	public Iterator<Entry<K, V>> iterator() {
		return new Iterator<Map.Entry<K,V>>() {
			private int k = 0;
			private int v = 0;
      public boolean hasNext() {
      	if (v >= values.length) { k++; v = 0; }
	      return k < keys.length;
      }
      @SuppressWarnings("unchecked")
      public Entry<K, V> next() {
	      return immutableEntry((K) keys[k], (V) values[k][v++]);
      }
      public void remove() {
      	throw new UnsupportedOperationException();
      }
		};
	}
	public SMap<K, V> add(K key, V value) {
		for (int i = 0; i < keys.length; i++) {
			if (equal(keys[i], key)) {
				Object[][] newValues = Arrays.copyOf(values, values.length);
				newValues[i] = concat(values[i], value); 
				return new MultiKeyMultiValue<K, V>(keys, newValues);
			}
		}
		return new MultiKeyMultiValue<K, V>(concat(keys, key), concat(values, new Object[] { value }));
	}
	public SMap<K, V> doAddAll(K key, Collection<? extends V> values) {
		for (int i = 0; i < keys.length; i++) {
			if (equal(keys[i], key)) {
				Object[][] newValues = Arrays.copyOf(this.values, this.values.length);
				newValues[i] = concat(this.values[i], values); 
				return new MultiKeyMultiValue<K, V>(keys, newValues);
			}
		}
		return new MultiKeyMultiValue<K, V>(concat(keys, key), concat(this.values, values.toArray()));
	}
}