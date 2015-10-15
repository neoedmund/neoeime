package neoe.ime.cn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import neoe.ime.FileUtil;
import neoe.ime.ImeLib;
import neoe.ime.ImeUnit;
import neoe.ime.MultiValueMap;
import neoe.ime.Res;
import neoe.ime.U;

public class CnWordLib implements ImeLib {

	private static CnCharLib charLib;

	private static MultiValueMap map;

	private static String getPy(String w) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < w.length(); i++) {
			char c = w.charAt(i);
			String py = CnCharLib.reverse(c);
			if (py == null) {
				return null;
			}
			sb.append(py);
		}
		return sb.toString();
	}

	private static String getPyHead(String w) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < w.length(); i++) {
			char c = w.charAt(i);
			String py = CnCharLib.reverse(c);
			if (py == null) {
				return null;
			}
			if (!py.isEmpty()) {
				sb.append(py.charAt(0));
			}
		}
		return sb.toString();
	}

	private static void init() throws IOException {
		System.out.print("load...");
		long t1 = System.currentTimeMillis();
		map = new MultiValueMap();
		String[] words = FileUtil.readString(U.getInstalledInputStream(Res.CN_DICT), null).split("\n");
		// writeToWordFile(words);
		// String fn="w2";
		// BufferedWriter out=new BufferedWriter(new OutputStreamWriter(new
		// FileOutputStream(fn),"utf8"));
		int wc = 0;
		int wcc = 0;
		for (String w : words) {
			w = w.trim();
			int p1 = w.indexOf(" ");
			String py;
			String pyHead = null;
			if (p1 > 0) {
				// set spell
				py = w.substring(p1 + 1).trim();
				w = w.substring(0, p1);
			} else {
				// auto spell
				py = getPy(w);
				pyHead = getPyHead(w);
			}
			if (pyHead != null) {
				map.add(pyHead, new ImeUnit(w, pyHead.length()));
			}
			if (py != null) {
				map.add(py, new ImeUnit(w, py.length()));
				wcc++;
			} else {
				System.out.println("drop " + w + " " + (wc++));
			}
		}
		map.sortAfterAddsDone();
		// out.close();
		System.out.println("cn_word " + wcc + " words in " + (System.currentTimeMillis() - t1) + " ms");
	}

	private Thread t1;

	public CnWordLib(CnCharLib charLib) throws Exception {
		if (map == null) {
			CnWordLib.charLib = charLib;
			charLib.getInitThread().join();
			t1 = new Thread() {
				public void run() {
					try {
						CnWordLib.init();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			t1.start();
		}
	}

	public List find(String py) {
		List exact = map.get(py);
		List partial = findPartial(py);
		if (exact.isEmpty())
			return partial;
		if (partial.isEmpty())
			return exact;
		List add = new ArrayList(exact);
		add.addAll(partial);
		return add;
	}

	private List findPartial(String py) {
		return map.getPartialValues(py);
	}

	@Override
	public Thread getInitThread() {
		return t1;
	}

	public static void main(String[] args) throws Exception {
		CnWordLib lib = new CnWordLib(new CnCharLib());
		lib.getInitThread().join();
		System.out.println(lib.find("jihao"));
	}
}

// OK: add partial(eg, niuxi),
// OK: add first letter word (eg, nxd)
