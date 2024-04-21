package neoe.ime.jp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import neoe.ime.FileUtil;
import neoe.ime.ImeLib;
import neoe.ime.ImeUnit;
import neoe.ime.MultiValueMap;
import neoe.ime.Res;
import neoe.ime.U;

public class JpWordLib implements ImeLib {
	private static JpCharLib charLib;

	public static MultiValueMap map;

	/**
	 * @param kana
	 * @param py
	 * @param kana
	 * @return
	 */
	private static void addImeUnit(String w, String py, String kana) {
		int len = py.length();

		char lastc = kana.charAt(kana.length() - 1);
		if (lastc >= 'a' && lastc <= 'z') {
			len--;
			putMultiValueMap(map, py, new ImeUnit(w, len));
		} else {
			putMultiValueMap(map, py, new ImeUnit(w, len));
			if (!kana.equals(w)) {
				putMultiValueMap(map, py, new ImeUnit(kana, len));
			}
		}
	}

	private static void putMultiValueMap(MultiValueMap m, String py, ImeUnit imeUnit) {
		m.add(py, imeUnit);
	}

	/**
	 * @throws IOException
	 * 
	 */
	protected static void init() throws IOException {
		System.out.print("load jp...");
		long t1 = System.currentTimeMillis();
		map = new MultiValueMap();
		String[] words = FileUtil.readString(U.getInstalledInputStream(Res.JP_DICT), null).split("\n");
		int wc = 0;
		int wcc = 0;
		for (String w : words) {
			w = w.trim();
			int p1 = w.indexOf(" ");
			String kanaStr;
			if (p1 > 0) {
				// set spell
				kanaStr = w.substring(p1 + 1);
				w = w.substring(0, p1);
			} else {
				// must be a pure kana word
				kanaStr = w;
			}
			if (kanaStr != null) {
				String[] kanaArr = kanaStr.split("\\／");
				for (int yi = 0; yi < kanaArr.length; yi++) {
					String kana = kanaArr[yi];
					String py = charLib.revShow(kana);
					addImeUnit(w, py, kana);
				}
				wcc++;
			} else {
				System.out.println("drop " + w + " " + (wc++));
			}
		}
		// out.close();
		map.sortAfterAddsDone();
		System.out.println("jp_word " + wcc + " words in " + (System.currentTimeMillis() - t1) + " ms");

	}

	public static void main(String[] args) throws Exception {
		JpWordLib lib = new JpWordLib(new JpCharLib());

	}

	private Thread t1;

	public JpWordLib(JpCharLib charLib) throws Exception {
		if (map == null) {
			map = new MultiValueMap();
			JpWordLib.charLib = charLib;
			charLib.getInitThread().join();
			t1 = new Thread() {
				public void run() {
					try {
						JpWordLib.init();
						// System.out.println("test="+find("senken"));
						// System.out.println("test="+find("gozai"));
						// System.out.println("test="+find("desu"));
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
		List partial = map.getPartialValues(py);
		if (exact.isEmpty())
			return partial;
		if (partial.isEmpty())
			return exact;
		List add = new ArrayList(exact);
		add.addAll(partial);
		return add;
//		List r = (List) map.get(py);
//		if (r == null) {
//			r = new ArrayList();
//		}
//		return r;
	}

	@Override
	public Thread getInitThread() {
		return t1;
	}

}
