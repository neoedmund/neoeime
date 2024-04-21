package neoe.ime.en;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import neoe.ime.ImeLib;
import neoe.ime.ImeUnit;
import neoe.ime.cnen.CnEnDict;

public class EnWord implements ImeLib {

	private CnEnDict dict;
	private Thread initThread;

	public EnWord(CnEnDict cnenDict) {
		this.dict = cnenDict;
		initThread = new Thread() {
			public void run() {
				try {
					dict.getInitThread().join();
					doInit();
					System.out.println("English(check) init size=" + words.size());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		initThread.start();
	}

	public static List words;

	private void doInit() {
		if (words != null)
			return;
		words = new ArrayList();
		for (Object o : dict.lines) {
			words.add(o);
		}
		Collections.sort(words, new Comparator() {
			@Override
			public int compare(Object o1, Object o2) {
				String s1 = (String) ((Object[]) o1)[3];
				String s2 = (String) ((Object[]) o2)[3];
				return s1.compareTo(s2);
			}
		});

	}

	@Override
	public List find(String py) {
		int p1 = 0;
		int p2 = words.size() - 1;
		int p = find(py, p1, p2);
		if (p == -1)
			return Collections.EMPTY_LIST;
		int q = p + 9;
		if (q > words.size())
			q = words.size();
//		System.out.println("[" + p + "," + q + "]");
		List res = new ArrayList();
		for (int i = p; i < q; i++) {
//			Object[] o = (Object[]) words.get(i);
			res.add(new ImeUnit(vx(words, i, 0) + "\t" + vx(words, i, 1), py.length()));
		}
		return res;
	}

	private int find(String py, int p1, int p2) {
		String s1 = vx(words, p1, 3);
		String s2 = vx(words, p2, 3);
		py = py.toLowerCase();
		if (s1.startsWith(py))
			return p1;
		if (s1.compareTo(py) > 0)
			return -1;
		if (py.compareTo(s2) > 0)
			return -1;
		int mid = (p1 + p2) / 2;
		if (mid == p1 || mid == p2)
			return p2;
		String sm = vx(words, mid, 3);
		if (sm.compareTo(py) > 0) {
			return find(py, p1, mid);
		} else {
			return find(py, mid, p2);
		}
	}

	private String vx(List w, int p, int i) {
		return (String) ((Object[]) w.get(p))[i];
	}

	@Override
	public Thread getInitThread() {
		return initThread;
	}
	
}
