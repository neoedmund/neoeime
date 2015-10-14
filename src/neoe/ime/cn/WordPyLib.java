package neoe.ime.cn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import neoe.ime.FileUtil;
import neoe.ime.ImeLib;
import neoe.ime.ImeUnit;
import neoe.ime.Res;
import neoe.ime.U;

public class WordPyLib implements ImeLib {

	private static CharPyLib charLib;

	private static Map map;

	private static String getPy(String w) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < w.length(); i++) {
			String c = "" + w.charAt(i);
			String py = CharPyLib.reverse(c);
			if (py == null) {
				return null;
			}
			sb.append(py);
		}
		return sb.toString();
	}

	private static void init() throws IOException {
		System.out.print("load...");
		long t1 = System.currentTimeMillis();
		map = new HashMap();
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
			if (p1 > 0) {
				// set spell
				py = w.substring(p1 + 1);
				w = w.substring(0, p1);
			} else {
				// auto spell
				py = getPy(w);
			}
			if (py != null) {
				List list = (List) map.get(py);
				if (list == null) {
					list = new ArrayList();
					map.put(py, list);
				}
				list.add(new ImeUnit(w, py.length()));
				// out.write(w);
				// out.write("\r\n");
				wcc++;
			} else {
				System.out.println("drop " + w + " " + (wc++));
			}
		}
		// out.close();
		System.out.println("cn_word " + wcc + " words in " + (System.currentTimeMillis() - t1) + " ms");
	}

	private Thread t1;

	public WordPyLib(CharPyLib charLib) throws IOException {
		if (map == null) {
			map = new HashMap();
			WordPyLib.charLib = charLib;
			t1= new Thread() {
				public void run() {
					try {
						WordPyLib.init();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			t1.start();
		}
	}

	public List find(String py) {
		List r = (List) map.get(py);
		if (r == null) {
			r = new ArrayList();
		}
		return r;
	}

	@Override
	public Thread getInitThread() {
		return t1;
	}

}
