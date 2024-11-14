/*
 * Created on 2006-2-25
 *
 * 
 */
package neoe.ime.jp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import neoe.ime.FileUtil;
import neoe.ime.ImeLib;
import neoe.ime.ImeUnit;
import neoe.ime.Res;
import neoe.ime.U;

/**
 * @author Fantasy
 * 
 * 
 */
public class JpCharLib implements ImeLib {
	public static HashMap pyMap;

	private static HashMap pyRevMap;

	/**
	 * @throws IOException
	 * 
	 */
	private static void init() throws IOException {
		String line;
		String[] lines = FileUtil.readString(U.getInstalledInputStream(Res.JP_CHAR), null).split("\n");
		pyMap = new HashMap();
		pyRevMap = new HashMap();
		int i = 0;
		while (i < lines.length) {
			line = lines[i++].trim();
			String line2 = lines[i++].trim();
			String line3 = lines[i++].trim();
			U.putMultiValueMap(pyMap, line3, line);
			U.putMultiValueMap(pyMap, line3, line2);
			U.putMultiValueMap(pyRevMap, line, line3);
			U.putMultiValueMap(pyRevMap, line2, line3);
		}
		System.out.println("jp_char size=" + pyMap.size());
	}

	private Thread t1;

	public JpCharLib() {
		if (pyMap == null) {
			pyMap = new HashMap();
			t1 = new Thread() {
				public void run() {
					try {
						JpCharLib.init();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			t1.start();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see neoe.ime.ImeLib#find(java.lang.String)
	 */
	public List find(String py) {
		List res = new ArrayList();
		List l = (List) pyMap.get(py);
		if (l == null) {
			return res;
		} else {
			int len = py.length();
			for (int i = 0; i < l.size(); i++) {
				String s = (String) l.get(i);
				res.add(new ImeUnit(s, len));
			}
		}
		return res;
	}

	String revShow(String kana) {
		StringBuffer sb = new StringBuffer();
		boolean xtu = false;
		for (int i = 0; i < kana.length(); i++) {
			String c = "" + kana.charAt(i);
			if (c.equals("っ")) {
				xtu = true;
				if (i == kana.length() - 1) {
					sb.append("xtu");
					continue;
				} else {
					continue;
				}
			}
//			if (i < kana.length() - 1) {
//				String c2 = c + kana.charAt(i + 1);
//				List list = (List) pyRevMap.get(c2);
//				if (list != null && list.size() > 0) {
//					i++;
//					if (xtu) {
//						xtu = false;
//						sb.append(((String) list.get(0)).charAt(0));
//					}
//					sb.append(list.get(0));
//					continue;
//				}
//
//			}
			List list = (List) pyRevMap.get(c);
			if (list != null && list.size() > 0) {
				if (xtu) {
					xtu = false;
					sb.append(((String) list.get(0)).charAt(0));
				}
				sb.append(list.get(0));
			} else {
				// System.out.println("cannot convert ["+c+"]");
				sb.append(c);
			}
		}
		return sb.toString();
	}

	@Override
	public Thread getInitThread() {
		return t1;
	}

}
