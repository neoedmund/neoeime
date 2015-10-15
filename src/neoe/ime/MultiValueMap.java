package neoe.ime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * multi-valued. and search-able.
 */
public class MultiValueMap<K extends String, V> {

	private HashMap<K, List<V>> data;
	private HashMap<K, Set<V>> ex;
	private List<K> sortedKey;

	public void sortAfterAddsDone() {
		sortedKey = new ArrayList<K>(data.keySet());
		Collections.sort(sortedKey);
	}

	public List<V> getPartialValues(K key) {
		if (sortedKey == null) {
			throw new RuntimeException(
					"please call sortAfterAddsDone() before me, and don't do it automatically because just need sort once.");
		}
		List<K> pk = getPartialMatchedKeys(key);
		NoDupList ret = new NoDupList();
		for (K k : pk) {
			ret.addAll(get(k));
		}
		return ret.data;
	}

	private List<K> getPartialMatchedKeys(K key) {
		List<K> list = sortedKey;
		if (list.isEmpty())
			return list;
		int p1 = 0;
		int p2 = list.size() - 1;
		int p3 = 0;
		while (true) {
			if (p1 == p2) {
				p3 = p1;
				break;
			}
			K o1 = list.get(p1);
			K o2 = list.get(p2);
			if (o1.compareTo(key) >= 0) {
				p3 = p1;
				break;
			}
			if (key.compareTo(o2) >= 0) {
				p3 = p2;
				break;
			}
			p3 = (p1 + p2) / 2;
			if (p3 <= p1) {
				break;
			}
			K o3 = list.get(p3);
			if (o3.compareTo(key) > 0) {
				p2 = p3;
				continue;
			} else {
				p1 = p3;
				continue;
			}
		}
		int p4 = p3 + 1;
		while (p4 < list.size()) {
			K o4 = list.get(p4);
			if (o4.startsWith(key))
				p4++;
			else {
				break;
			}
		}
		if (p3 < p4 - 1) {
			if (!list.get(p3).startsWith(key))
				p3++;
		}
		return list.subList(p3, p4);
	}

	public MultiValueMap() {
		data = new HashMap<K, List<V>>();
		ex = new HashMap<K, Set<V>>();
	}

	public List<V> get(K key) {
		List<V> ret = data.get(key);
		if (ret == null)
			return Collections.emptyList();
		return ret;
	}

	public boolean add(K key, V value) {
		List<V> ret = data.get(key);
		Set<V> exi;
		if (ret == null) {
			exi = new HashSet<V>();
			ret = new ArrayList<V>();
			data.put(key, ret);
			ex.put(key, exi);
		} else {
			exi = ex.get(key);
			if (exi.contains(value))
				return false;
		}
		exi.add(value);
		ret.add(value);
		return true;
	}
}
