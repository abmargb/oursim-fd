package br.edu.ufcg.lsd.oursim.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.TreeBidiMap;

public class BidirectionalMap<K, V> implements Map<K, V> {

	private BidiMap bidiMap;

	public BidirectionalMap() {
		this.bidiMap = new TreeBidiMap();
	}

	@Override
	public void clear() {
		bidiMap.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return bidiMap.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return bidiMap.containsValue(value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return bidiMap.entrySet();
	}

	@SuppressWarnings("unchecked")
	public K getKey(V v) {
		return (K) bidiMap.getKey(v);
	}

	@SuppressWarnings("unchecked")
	@Override
	public V get(Object key) {
		return (V) bidiMap.get(key);
	}

	@Override
	public boolean isEmpty() {
		return bidiMap.isEmpty();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<K> keySet() {
		return bidiMap.keySet();
	}

	@SuppressWarnings("unchecked")
	@Override
	public V put(K key, V value) {
		assert key != null && value != null;
		assert !bidiMap.containsKey(key) : key;
		assert !bidiMap.containsValue(value) : value;
		return (V) bidiMap.put(key, value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		bidiMap.putAll(m);
	}

	@SuppressWarnings("unchecked")
	@Override
	public V remove(Object key) {
		assert bidiMap.containsKey(key);
		V removed = (V) bidiMap.remove(key);
		assert !bidiMap.containsValue(removed);
		assert !bidiMap.containsKey(key);
		return removed;
	}

	@Override
	public int size() {
		return bidiMap.size();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<V> values() {
		return bidiMap.values();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String sep = "";
		for (Entry<K, V> entry : entrySet()) {
			sb.append(sep).append("   " + entry.getKey() + " -> " + entry.getValue());
			sep = "\n";
		}
		return sb.toString();
	}

}
