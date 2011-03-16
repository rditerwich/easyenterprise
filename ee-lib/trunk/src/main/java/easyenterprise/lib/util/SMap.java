package easyenterprise.lib.util;

import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Maps.immutableEntry;
import static easyenterprise.lib.util.CollectionUtil.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

/**
 * Simple, Small, Static key-value map-like class. It's optimized for 
 * memory usage (and transport size), and is tailored for the 'simple'
 * cases: when there is only one item, all items have null keys, all 
 * items have the same key, or when there is only one value per key.
 * Large content is inefficient, specially when building them. Class
 * is particularly useful for caching and transport purposes.   
 * 
 * All keys and values remain ordered according to order of insertion.
 * If a key is re-inserted, its position will remain the same.
 */
public abstract class SMap<K, V> implements Iterable<Entry<K, V>>, Serializable {
 
	private static final long serialVersionUID = 1L;
	private static final Object  undefined = new Object();
	private static SMap<?, ?> emptyMap = new SMapEmpty<Object, Object>(); 
	
	@SuppressWarnings("unchecked")
	public static <K, V> SMap<K, V> empty() {
		return (SMap<K, V>) emptyMap;
	}
	
	
	@SuppressWarnings("unchecked")
	public static <K, V> SMap<K, V> create(K key, V value) {
		return (SMap<K, V>) empty().add(key, value);
	}
	
	@SuppressWarnings("unchecked")
	public static <K, V> SMap<K, V> createAll(K key, Collection<? extends V> values) {
		return (SMap<K, V>) empty().addAll(key, values);
	}
	
	/**
	 * When the map is empty there are no keys and no values.
	 */
	public boolean isEmpty() {
		return size() == 0;
	}
	
	/**
	 * Returns the key with given index. Note that null is returned either
	 * when the first key is null or when the index is invalid.
	 * Use {@link #getKeys()} to make the distinction. 
	 * @return Key or null.
	 */
	public abstract K getKey(int index);
	
	
	public Map.Entry<K, V> getEntry(int index) {
		return immutableEntry(getKey(index), get(index));
	}

	/**
	 * Returns the keys for which the map has values. There
	 * will be at least one value for these keys, albeit the
	 * null value.
	 * @return Keys or the empty list.
	 */
	public abstract List<K> getKeys();

	/**
	 * Returns the number of keys in this map. 
	 * @return Number of keys.
	 */
	public abstract int size();
	
	/**
	 * Returns the value corresponding to the null key.
	 * @return Value or null.
	 */
	public final V get() {
		return get(null, null);
	}
	
	/**
	 * Returns the first value for given key. Note the null is
	 * returned either when the first value for given key is null
	 * or when there are no values for given key. 
	 * Use {@link #getAll(Object)} or {@link #get(Object, Object)}
	 * to make the distinction.
	 * @param key
	 * @param defaultValue TODO
	 * @return Value or null.
	 */
	public final V get(K key) {
		return get(key, null);
	}
	
	public V get(int index) {
		return get(index, null);
	}
	
	/**
	 * Returns the first value for given key. If the key does
	 * not exist, the default value is returned.
	 * @param key
	 * @param defaultValue 
	 * @return Value or null.
	 */
	public abstract V get(K key, V defaultValue);
	
	/**
	 * Returns the first value for given key index. If the index is not valid, 
	 * the default value is returned. 
	 * @param key
	 * @param defaultValue 
	 * @return Value or null.
	 */
	public abstract V get(int index, V defaultValue);

	/**
	 * Warning: only call when V is a nested SMap.
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public V getOrEmpty(K key) {
		return get(key, (V) emptyMap);
	}
	
	/**
	 * 
	 * @param keys
	 * @return Value or null
	 */
	public V tryGet(K... keys) {
		@SuppressWarnings("unchecked")
		V undefined = (V) SMap.undefined;
		for (K key : keys) {
			V value = get(key, undefined);
			if (value != undefined) {
				return value;
			}
		}
		return null;
	}
	
	/**
	 * Returns the values corresponding to the null key.
	 * @return Values or the empty list
	 */
	public List<V> getAll() {
		return getAll(null);
	}
	
	/**
	 * Returns the values for given key. 
	 * @param key
	 * @return Values or the empty list.
	 */
	public abstract List<V> getAll(K key);
	
	/**
	 * Returns the values for given key. When the index is invalid, 
	 * the empty list is returned. 
	 * @param key
	 * @return Values or the empty list.
	 */
	public abstract List<V> getAll(int index);
	
	/**
	 * Tries every key, returns the first
	 * values found.
	 * @param keys
	 * @return Values or empty list
	 */
	public List<V> tryGetAll(K... keys) {
		for (K key : keys) {
			List<V> values = getAll(key);
			if (!values.isEmpty()) {
				return values;
			}
		}
		return Collections.emptyList();
	}
	
	public int indexOf(K key) {
		for (int i = 0; i < size(); i++) {
			if (equal(getKey(i), key)) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Synonym to add(null, value)
	 */
	public SMap<K, V> add(V value) {
		return add(null, value);
	}

	/**
	 * Adds a single key/value to the map.  If the key already exists, its position in the keylist will remain the same.
	 * @param key
	 * @param value
	 * @return
	 */
	public abstract SMap<K, V> add(K key, V value);
	
	public abstract SMap<K, V> set(K key, V value);
	
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
	
	public SMap<K, V> addAll(SMap<K, V> values) {
		SMap<K, V> map = this;
		for (Entry<K, V> entry : values) {
			map = map.add(entry.getKey(), entry.getValue());
		}
		return map;
	}
	
	public SMap<K, V> setAll(SMap<K, V> values) {
		SMap<K, V> map = this;
		for (Entry<K, V> entry : values) {
			map = map.set(entry.getKey(), entry.getValue());
		}
		return map;
	}
	
	public SMap<K, V> addAll(Map<K, V> values) {
		SMap<K, V> map = this;
		for (Entry<K, V> entry : values.entrySet()) {
			map = map.add(entry.getKey(), entry.getValue());
		}
		return map;
	}
	
	public SMap<K, V> removeKey(K key) {
		return removeKey(indexOf(key));
	}
	
	public SMap<K, V> removeKey(int index) {
		SMap<K, V> result = SMap.empty();
		for (int i = 0; i < size(); i++) {
			if (i != index) {
				result = result.addAll(getKey(i), getAll(i));
			}
		}
		return result;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object other) {
		if (other instanceof SMap && other.getClass() == getClass()) {
			return Lists.newArrayList(this).equals(Lists.newArrayList((SMap<K, V>) other));
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Lists.newArrayList(this).hashCode();
	}

	@SuppressWarnings("unchecked")
	public static <V> V undefined() {
		return (V) undefined;
	}
	
	/**
	 * It's guaranteed that values.size() > 1 
	 */
	protected abstract SMap<K, V> doAddAll(K key, Collection<? extends V> values);

	Object[] concat(Object[] values, Object value) {
		Object[] copy = SMap.copyOf(values, values.length + 1);
		copy[values.length] = value;
		return copy;
	}
	
	Object[][] concat(Object[][] values, Object[] value) {
		Object[][] copy = SMap.copyOf(values, values.length + 1);
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
	
	Object[] concat(Object[] values, Collection<? extends Object> values2) {
		Object[] copy = SMap.copyOf(values, values.length + values2.size());
		int i = values.length;
		for (Object value2 : values) {
			copy[i++] = value2;
		}
		return copy;
	}
	
    static  Object[] copyOf(Object[] original, int newLength) {
    	Object[] copy = new Object[newLength];
        System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
        return copy;
    }

    static  Object[][] copyOf(Object[][] original, int newLength) {
    	Object[][] copy = new Object[newLength][];
    	System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
    	return copy;
    }
}

class SMapEmpty<K, V> extends SMap<K, V> {
	private static final long serialVersionUID = 1L;
	public int size() {
		return 0;
	}
	public K getKey(int index) {
	  return null;
	}
	public List<K> getKeys() {
		return emptyList();
	}
	public V get(K key, V defaultValue) {
		return defaultValue;
	}
	public V get(int index, V defaultValue) {
		return defaultValue;
	}
	public List<V> getAll(K key) {
		return emptyList();
	}
	public List<V> getAll(int index) {
		return emptyList();
	}
	public Iterator<Entry<K, V>> iterator() {
	  return Collections.<Entry<K, V>>emptyList().iterator();
	}
	public SMap<K, V> add(K key, V value) {
		if (key == null) return new SMapNoKeySingleValue<K, V>(value);
		else return new SMapSingleKeySingleValue<K, V>(key, value); 
	}
	public SMap<K, V> set(K key, V value) {
		return add(key, value);
	}
	public SMap<K,V> doAddAll(K key, Collection<? extends V> values) {
		if (key == null) return new SMapNoKeyMultiValue<K, V>(values.toArray());
		else return new SMapSingleKeyMultiValue<K, V>(key, values.toArray());
	};
	@Override
	public String toString() {
		return "[]";
	}
}

class SMapNoKeySingleValue<K, V> extends SMap<K, V> {
	private static final long serialVersionUID = 1L;
	private static final List<?> nullList = singletonList(null);
	private V value;
	public SMapNoKeySingleValue() {
	}
	public SMapNoKeySingleValue(V value) {
		this.value = value;
	}
	public int size() {
		return 1;
	}
	public K getKey(int index) {
		return null;
	}
	@SuppressWarnings("unchecked")
  public List<K> getKeys() {
		return (List<K>) nullList;
	}
	public V get(K key, V defaultValue) {
		return key == null ? value : defaultValue;
	}
	public V get(int index, V defaultValue) {
		return index == 0 ? value : defaultValue;
	}
	public List<V> getAll(K key) {
		if (key != null) return emptyList();
		return singletonList(value);
	}
	public List<V> getAll(int index) {
		if (index == 0) return singletonList(value);
		else return emptyList();
	}
	public Iterator<Entry<K, V>> iterator() {
	  return singleton(immutableEntry((K) null, value)).iterator();
	}
	public SMap<K, V> add(K key, V value) {
		if (key == null) return new SMapNoKeyMultiValue<K, V>(this.value, value); 
		else return new SMapMultiKeySingleValue<K, V>(new Object[] { null, key}, new Object[] { this.value, value }); 
	}
	public SMap<K, V> set(K key, V value) {
		if (key == null)
			if (equal(this.value, value)) return this;
			else return new SMapNoKeySingleValue<K, V>(value);
		return add(key, value);
	}
	public SMap<K,V> doAddAll(K key, Collection<? extends V> values) {
		if (key == null) return new SMapNoKeyMultiValue<K, V>(null, this.value, value); 
		else return new SMapMultiKeySingleValue<K, V>(new Object[] { null, key}, new Object[] { this.value, value }); 
	};
	@Override
	public String toString() {
		return "[(null, " + value + ")]";
	}
}

class SMapSingleKeySingleValue<K, V> extends SMap<K, V> {
	private static final long serialVersionUID = 1L;
	private K key;
	private V value;
	SMapSingleKeySingleValue() {
	}
	SMapSingleKeySingleValue(K key, V value) {
		this.key = key;
		this.value = value;
	}
	public int size() {
		return 1;
	}
	public K getKey(int index) {
	  return index == 0 ? this.key : null;
	}
	public List<K> getKeys() {
		return singletonList(key);
	}
	public V get(K key, V defaultValue) {
		if (equal(this.key, key)) return value;
		else return defaultValue;
	}
	public V get(int index, V defaultValue) {
		if (index == 0) return value;
		else return defaultValue;
	}
	public List<V> getAll(K key) {
		if (equal(this.key, key)) return singletonList(value);
		else return emptyList();
	}
	public List<V> getAll(int index) {
		if (index == 0) return singletonList(value);
		else return emptyList();
	}
	public Iterator<Entry<K, V>> iterator() {
	  return singleton(immutableEntry(key, value)).iterator();
	}
	public SMap<K, V> add(K key, V value) {
		if (equal(this.key, key)) return new SMapSingleKeyMultiValue<K, V>(key, this.value, value); 
		else return new SMapMultiKeySingleValue<K, V>(new Object[] { this.key, key }, new Object[] { this.value, value });
	}
	public SMap<K,V> set(K key, V value) {
		if (equal(this.key, key))
			if (equal(this.value, value)) return this;
			else return new SMapSingleKeySingleValue<K, V>(key, value);
		else return add(key, value);
	};
	public SMap<K, V> doAddAll(K key, Collection<? extends V> values) {
		if (equal(this.key, key)) return new SMapSingleKeyMultiValue<K, V>(key, concat(this.value, values)); 
		else return new SMapMultiKeyMultiValue<K, V>(new Object[] { this.key, key }, new Object[][] { new Object[] { this.value }, values.toArray() });
	}
	@Override
	public String toString() {
		return "[(" + key + ", " + value + ")]";
	}
}

class SMapMultiKeySingleValue<K, V> extends SMap<K, V> {
	private static final long serialVersionUID = 1L;
	transient Object[] keys;
	transient Object[] values;
	SMapMultiKeySingleValue() {
	}
	SMapMultiKeySingleValue(Object[] keys, Object[] values) {
		this.keys = keys;
		this.values = values;
	}
	@Override
	public int size() {
		return keys.length;
	}
	@SuppressWarnings("unchecked")
	public K getKey(int index) {
		if (index >= 0 && index < keys.length) return (K) keys[index];
		else return null;
	}
	@SuppressWarnings("unchecked")
  public List<K> getKeys() {
		return (List<K>) asList(keys);
	}
	@SuppressWarnings("unchecked")
  public V get(K key, V defaultValue) {
		for (int i = 0; i < keys.length; i++) {
			if (Objects.equal(keys[i], key)) {
				return (V) values[i];
			}
		}
		return defaultValue;
	}
	@SuppressWarnings("unchecked")
  public V get(int index, V defaultValue) {
		if (index >= 0 && index < keys.length) {
			return (V) values[index];
		}
		return defaultValue;
	}
	@SuppressWarnings("unchecked")
	public List<V> getAll(K key) {
		for (int i = 0; i < keys.length; i++) {
			if (equal(keys[i], key)) {
				return (List<V>) asList(values[i]);
			}
		}
		return emptyList();
	}
	@SuppressWarnings("unchecked")
	public List<V> getAll(int index) {
		if (index >= 0 && index < keys.length) return(List<V>) asList(values[index]);
		else return emptyList();
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
				return new SMapMultiKeyMultiValue<K, V>(keys, wrapArray(i, new Object[] { values[i], value } ));
		}
		return new SMapMultiKeySingleValue<K, V>(concat(keys, key), concat(values, value));
	}
	public SMap<K, V> set(K key, V value) {
		for (int i = 0; i < keys.length; i++) {
			if (equal(keys[i], key)) {
				if (equal(values[i], value)) return this;
				Object[] newValues = SMap.copyOf(values, values.length);
				newValues[i] = value; 
				return new SMapMultiKeySingleValue<K, V>(keys, newValues);
			}
		}
		return add(key, value);
	}
	
	public SMap<K, V> doAddAll(K key, Collection<? extends V> values) {
		for (int i = 0; i < keys.length; i++) {
			if (equal(keys[i], key))
				return new SMapMultiKeyMultiValue<K, V>(keys, wrapArray(i, concat(this.values[i], values)));
		}
		return new SMapMultiKeySingleValue<K, V>(concat(keys, key), concat(this.values, values));
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[");
		String sep = "";
		for (int i = 0; i < keys.length; i++) {
			result.append(sep); sep = ", ";
			result.append("(").append(keys[i]).append(", ").append(values[i]).append(")");
		}
		result.append("]");
		return result.toString();
	}
	
	private Object[][] wrapArray(int index, Object[] arrayAtIndex) {
		Object[][] valueArrays = new Object[keys.length][];
		for (int i = 0; i < keys.length; i++) {
			valueArrays[i] = index == i ? arrayAtIndex : new Object[] { values[i] };
		}
		return valueArrays;
	}
}

class SMapNoKeyMultiValue<K, V> extends SMap<K, V> {
	private static final long serialVersionUID = 1L;
	private static List<?> nullList = singletonList(null);
	transient Object[] values;
	SMapNoKeyMultiValue() {
	}
	SMapNoKeyMultiValue(Object... values) {
		this.values = values;
	}
	@Override
	public int size() {
		return 1;
	}
	public K getKey(int index) {
	  return null;
	}
	@SuppressWarnings("unchecked")
  public List<K> getKeys() {
		return (List<K>) nullList;
	}
	@SuppressWarnings("unchecked")
  public V get(K key, V defaultValue) {
		return (V) (key == null ? values[0] : defaultValue);
	};
	@SuppressWarnings("unchecked")
  public V get(int index, V defaultValue) {
		return (V) (index == 0 ? values[0] : defaultValue);
	}
	@SuppressWarnings("unchecked")
	public List<V> getAll(K key) {
		if (key != null) return emptyList();
		return (List<V>) asList(values);
	}
	@SuppressWarnings("unchecked")
	public List<V> getAll(int index) {
		if (index == 0) return (List<V>) asList(values);
		else return emptyList();
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
		if (key == null) return new SMapNoKeyMultiValue<K, V>(concat(values, value));
		else return new SMapMultiKeyMultiValue<K, V>(new Object[] { null, key }, new Object[][] { values, new Object[] { value } }); 
	}
	public SMap<K, V> set(K key, V value) {
		if (key == null) 
			if (values.length == 1 && equal(this.values[0], value)) return this;
			else return new SMapNoKeySingleValue<K, V>(value);
		else return add(key, value); 
	}
	protected SMap<K,V> doAddAll(K key, Collection<? extends V> values) {
		if (key == null)
			return new SMapNoKeyMultiValue<K, V>(key, concat(this.values, values));
		else
			return new SMapMultiKeyMultiValue<K, V>(new Object[] { null, key }, new Object[][] { this.values, values.toArray() }); 
	}
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[(null, [");
		String sep = "";
		for (int i = 0; i < values.length; i++) {
			result.append(sep); sep = ", ";
			result.append(values[i]);
		}
		
		result.append("])]");
		return result.toString();
	}
}

class SMapSingleKeyMultiValue<K, V> extends SMap<K, V> {
	private static final long serialVersionUID = 1L;
	transient K key;
	transient Object[] values;
	SMapSingleKeyMultiValue() {
	}
	SMapSingleKeyMultiValue(K key, Object... values) {
		this.key = key;
		this.values = values;
	}
	@Override
	public int size() {
		return 1;
	}
	public K getKey(int index) {
		return index == 0 ? this.key : null;
	}
	public List<K> getKeys() {
		return singletonList(key);
	}
	@SuppressWarnings("unchecked")
	public V get(K key, V defaultValue) {
		return (V) (key == null ? values[0] : defaultValue);
	};
	@SuppressWarnings("unchecked")
	public V get(int index, V defaultValue) {
		return (V) (index == 0 ? values[0] : defaultValue);
	};
	@SuppressWarnings("unchecked")
	public List<V> getAll(K key) {
		if (!equal(this.key, key)) return emptyList();
		return (List<V>) asList(values);
	}
	@SuppressWarnings("unchecked")
	public List<V> getAll(int index) {
		if (index != 0) return emptyList();
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
		if (equal(this.key, key)) return new SMapSingleKeyMultiValue<K, V>(key, concat(values, value));
		else return new SMapMultiKeyMultiValue<K, V>(new Object[] { this.key, key }, new Object[][] { values, new Object[] { value } }); 
	}
	public SMap<K, V> set(K key, V value) {
		if (equal(this.key, key))
			if (values.length == 1 && equal(this.values[0], value)) return this;
			else return new SMapSingleKeySingleValue<K, V>(key, value);
		else return add(key, value); 
	}
	protected SMap<K,V> doAddAll(K key, Collection<? extends V> values) {
		if (equal(this.key, key)) return new SMapSingleKeyMultiValue<K, V>(key, concat(this.values, values));
		else return new SMapMultiKeyMultiValue<K, V>(new Object[] { this.key, key }, new Object[][] { this.values, values.toArray() }); 
	}
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[(").append(key).append(", [");
		String sep = "";
		for (int i = 0; i < values.length; i++) {
			result.append(sep); sep = ", ";
			result.append(values[i]);
		}
		
		result.append("])]");
		return result.toString();
	}
}

class SMapMultiKeyMultiValue<K, V> extends SMap<K, V> {
	private static final long serialVersionUID = 1L;
	transient Object[] keys;
	transient Object[][] values;
	SMapMultiKeyMultiValue() {
	}
	SMapMultiKeyMultiValue(Object[] keys, Object[][] values) {
		this.keys = keys;
		this.values = values;
	}
	public int size() {
		return keys.length;
	}
	@SuppressWarnings("unchecked")
	public K getKey(int index) {
		if (index >= 0 && index < keys.length) return (K) keys[index];
		else return null;
	}
	@SuppressWarnings("unchecked")
	public List<K> getKeys() {
		return (List<K>) asList(keys);
	}
	@SuppressWarnings("unchecked")
  public V get(K key, V defaultValue) {
		for (int i = 0; i < keys.length; i++) {
			if (equal(keys[i], key))
				return (V) values[i][0];
		}
		return defaultValue;
	}
	@SuppressWarnings("unchecked")
	public V get(int index, V defaultValue) {
		if (index >= 0 && index < keys.length) return (V) values[index][0];
		else return defaultValue;
	}
	@SuppressWarnings("unchecked")
	public List<V> getAll(K key) {
		for (int i = 0; i < keys.length; i++) {
			if (equal(keys[i], key))
				return (List<V>) asList(values[i]);
		}
		return emptyList();
	}
	@SuppressWarnings("unchecked")
	public List<V> getAll(int index) {
		if (index >= 0 && index < keys.length) return (List<V>) asList(values[index]);
		else return emptyList();
	}
	public Iterator<Entry<K, V>> iterator() {
		return new Iterator<Map.Entry<K,V>>() {
			private int k = 0;
			private int v = 0;
      public boolean hasNext() {
      	if (v >= values[k].length) { k++; v = 0; }
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
				Object[][] newValues = SMap.copyOf(values, values.length);
				newValues[i] = concat(values[i], value); 
				return new SMapMultiKeyMultiValue<K, V>(keys, newValues);
			}
		}
		return new SMapMultiKeyMultiValue<K, V>(concat(keys, key), concat(values, new Object[] { value }));
	}
	public SMap<K, V> set(K key, V value) {
		for (int i = 0; i < keys.length; i++) {
			if (equal(keys[i], key)) {
				if (values[i].length == 1 && equal(values[i][0], value)) {
					return this;
				}
				Object[][] newValues = SMap.copyOf(values, values.length);
				newValues[i] = new Object[] { value }; 
				return new SMapMultiKeyMultiValue<K, V>(keys, newValues);
				// TODO compact to multikeysinglevalue
			}
		}
		return add(key, value);
	}
	public SMap<K, V> doAddAll(K key, Collection<? extends V> values) {
		for (int i = 0; i < keys.length; i++) {
			if (equal(keys[i], key)) {
				Object[][] newValues = SMap.copyOf(this.values, this.values.length);
				newValues[i] = concat(this.values[i], values); 
				return new SMapMultiKeyMultiValue<K, V>(keys, newValues);
			}
		}
		return new SMapMultiKeyMultiValue<K, V>(concat(keys, key), concat(this.values, values.toArray()));
	}
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[");
		String sep = "";
		for (int i = 0; i < keys.length; i++) {
			result.append(sep); sep = ", ";
			result.append("(").append(keys[i]).append(", [");
			String sep2 = "";
			for (int j = 0; j < values[i].length; j++) {
				result.append(sep2); sep2 = ", ";
				result.append(values[i][j]);
			}
			result.append("])");
		}
		
		result.append("]");
		return result.toString();
	}

}
