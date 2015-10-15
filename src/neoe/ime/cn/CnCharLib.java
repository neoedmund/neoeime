package neoe.ime.cn;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import neoe.ime.FileUtil;
import neoe.ime.ImeLib;
import neoe.ime.ImeUnit;
import neoe.ime.MultiValueMap;
import neoe.ime.Res;
import neoe.ime.U;

public class CnCharLib implements ImeLib {
	private static String pydata;

	 

	private static void readPy() throws IOException {
		pydata = FileUtil.readString(U.getInstalledInputStream(Res.CN_CHAR), null);
		init();
		System.out.println("cn_char inited " + pydata.length());
	}

	static MultiValueMap m;

	private static void init() {
		if (m != null)
			return;
		m = new MultiValueMap();
		int p0 = 0;
		String str = pydata;
		while (true) {
			int p1 = str.indexOf(' ', p0);
			int p2 = str.indexOf(' ', p1 + 1);
			if (p1 < 0 || p2 < 0) {
				System.out.println("waring:py data format error");
				break;
			}
			addInfo(str, p0, p1, p2);
			p0 = p2 + 1;
			if (p0 >= str.length())
				break;
		}

	}

	private static void addInfo(String s, int p0, int p1, int p2) {
		String py = s.substring(p1 + 1, p2);
		int len = py.length();
		for (int i = p0; i < p1; i++) {
			m.add(py, new ImeUnit("" + s.charAt(i), len));
		}
	}

	/** char 2 py */
	public static String reverse(char c) {
		int p = pydata.indexOf(c);
		if (p < 0) {
			return null;
		}
		int p2 = pydata.indexOf(" ", p + 1);
		int p3 = pydata.indexOf(" ", p2 + 1);
		return pydata.substring(p2 + 1, p3);
	}

	Thread initThread;

	public CnCharLib() throws IOException {
		if (pydata == null) {
			pydata = "";
			initThread = new Thread() {
				public void run() {
					try {
						readPy();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			initThread.start();
		}
	}

	public List find(String py) {
		List v = m.get(py);
		return v==null?Collections.EMPTY_LIST:v;
	}

	@Override
	public Thread getInitThread() {
		return initThread;
	}
}
