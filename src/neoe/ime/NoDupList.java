package neoe.ime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NoDupList<V> {

	/** gate keeper */
	Set<V> gk = new HashSet<V>();
	public List<V> data = new ArrayList<V>();

	public void addAll(List<V> list) {
		for (V v : list) {
			if (gk.contains(v))
				continue;
			gk.add(v);
			data.add(v);
		}
	}

}
