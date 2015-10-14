package neoe.ime;

import java.util.ArrayList;
import java.util.List;

import neoe.ime.cn.CharPyLib;
import neoe.ime.cn.WordPyLib;
import neoe.ime.jp.JpWordLib;
import neoe.ime.jp.KanaLib;
import neoe.ime.spi.NeoeInputMethod;

public class ImeImpB implements Ime {

	private static boolean initFinished = false, initStarted = false;

	private static ImeLib jpWord;

	private static ImeLib jpChar;

	private static ImeLib cnChar;

	private static ImeLib cnWord;
	private static final int CN = 1;
	private static final int JP = 2;
	private static final int MAX_OUT_LEN = 30;

	private static final int MAX_SELECTION = 10;

	public static void main(String[] args) throws Exception {
		ImeImpB b = new ImeImpB(null);
		b.imeType=JP;
		while (true) {
			if (b.initFinished)
				break;
			Thread.sleep(1000);
		}
		b.find("aiyisiniuxiaodong");
		while (true) {
			System.out.println(b.out());
			// b.next();
			System.out.println("s=" + b.select(0));
			if (b.getCount() == 0)
				break;
		}
	}

	private int c;

	private String cur = "";

	private NeoeInputMethod method;

	private List result = new ArrayList();

	private int start;
	int imeType;

	public ImeImpB(NeoeInputMethod method) {
		this.method = method;
		imeType = 1;
		if (!initStarted) {
			initStarted = true;
			init();
		}
	}

	public void find(String py) {
		int len = py.length();
		cur = py;
		result.clear();
		for (int i = len; i > 0; i--) {
			String sub = py.substring(0, i);
			if (imeType == CN) {
				result.addAll(cnWord.find(sub));
				result.addAll(cnChar.find(sub));
			} else if (imeType == JP) {// jp selected
				result.addAll(jpChar.find(sub));
				result.addAll(jpWord.find(sub));
			}
		}
		// System.out.println("count=" + result.size());
		start = 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see neoe.py.IME#getCount()
	 */
	public int getCount() {
		return result.size();
	}

	public String getCurrentPy() {
		return cur;
	}

	private String getTxt(int i) {
		if (i >= 0 && i < result.size()) {
			return ((ImeUnit) result.get(i)).txt;
		} else {
			return "NULL";
		}
	}

	private void init() {		
		try {
			cnChar = new CharPyLib();
			cnWord = new WordPyLib((CharPyLib) cnChar);
			jpChar = new KanaLib();
			jpWord = new JpWordLib((KanaLib) jpChar);

			ImeLib[] libs = new ImeLib[] { cnChar, cnWord, jpChar, jpWord };
			for (ImeLib lib : libs) {
				Thread t = lib.getInitThread();
				if (t != null) {
					t.join();// memory leaked, but should be no problem
				}
			}

			System.out.println("imeB init ok");
			initFinished = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void next() {
		StringBuffer sb = new StringBuffer();
		c = 0;
		for (; start + c < result.size();) {
			String s = getTxt(start + c);
			String h = c == 9 ? "0" : "" + (c + 1);
			s = h + s + " ";
			c++;
			if (sb.length() + s.length() >= MAX_OUT_LEN) {
				c--;
				break;
			} else {
				sb.append(s);
			}
			if (c >= MAX_SELECTION) {
				break;
			}
		}

		if (start + c < result.size()) {
			start += c;
		}
		// System.out.println(start);
	}

	public String out() {
		StringBuffer sb = new StringBuffer();
		c = 0;
		for (; start + c < result.size();) {
			String s = getTxt(start + c);
			String h = c == 9 ? "0" : "" + (c + 1);
			s = h + s + " ";
			c++;
			if (sb.length() + s.length() >= MAX_OUT_LEN) {
				c--;
				break;
			} else {
				sb.append(s);
			}
			if (c >= MAX_SELECTION) {
				break;
			}
		}

		if (start > 0) {
			sb.append("<< ");
		}
		if (start + c < result.size()) {
			sb.append(">> ");
		}
		return sb.toString();
	}

	public void prev() {
		StringBuffer sb = new StringBuffer();
		c = 0;
		for (; start - c >= 0;) {
			String s = getTxt(start - c);
			String h = c == 9 ? "0" : "" + (c + 1);
			s = h + s + " ";
			c++;
			if (sb.length() + s.length() >= MAX_OUT_LEN) {
				c--;
				break;
			} else {
				sb.append(s);
			}
			if (c >= MAX_SELECTION) {
				break;
			}
		}
		if (start - c >= 0) {
			start -= c;
		}
		// System.out.println(start);
	}

	public String select(int index) {
		// System.out.println("C=" + c);
		if (index < 0 || index > c - 1) {
			return "";
		}
		index += start;
		String res = "";
		// if (index >= 0 && index < result.size()) {
		ImeUnit u = (ImeUnit) result.get(index);
		res = u.txt;
		int v = cur.length() - u.pylen;
		if (v > 0) {
			cur = cur.substring(cur.length() - v);
			find(cur);
		} else {
			find("");
		}
		// }
		return res;
	}

}
