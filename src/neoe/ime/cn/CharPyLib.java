package neoe.ime.cn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import neoe.ime.FileUtil;
import neoe.ime.ImeLib;
import neoe.ime.ImeUnit;
import neoe.ime.Res;
import neoe.ime.U;

public class CharPyLib implements ImeLib {
	private static String pydata;

	public static String finds(String a) {
		String cur;
		// System.out.println("find [" + a + "]");
		int p = pydata.indexOf(" " + a + " ");
		if (p < 0) {
			cur = "";
		} else {
			int p2 = pydata.lastIndexOf(" ", p - 1);
			if (p2 < 0) {
				p2 = -1;
			}
			cur = pydata.substring(p2 + 1, p);
		}
		return cur;

	}

	private static void readPy() throws IOException {
		pydata = FileUtil.readString(U.getInstalledInputStream(Res.CN_CHAR),null);
		System.out.println("cn_char inited "+pydata.length());
	}

	/** char 2 py */
	public static String reverse(String c) {
		int p = pydata.indexOf(c);
		if (p < 0) {
			return null;
		}
		int p2 = pydata.indexOf(" ", p + 1);
		int p3 = pydata.indexOf(" ", p2 + 1);
		return pydata.substring(p2 + 1, p3);
	}

	private Thread initThread;

	public CharPyLib() throws IOException {
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
		String s = finds(py);
		List r = new ArrayList();
		int len = py.length();
		for (int i = 0; i < s.length(); i++) {
			r.add(new ImeUnit("" + s.charAt(i), len));
		}
		return r;
	}

	@Override
	public Thread getInitThread() {
		return initThread;
	}
}
