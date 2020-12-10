package neoe.ime.cnen;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import neoe.ime.ImeLib;
import neoe.ime.ImeUnit;
import neoe.ime.U;
import neoe.ime.cn.CnCharLib;

public class CnEnDict implements ImeLib {

	private Thread initThread;
	private CnCharLib cnChar;

	public CnEnDict(CnCharLib cnChar) {
		this.cnChar = cnChar;
		initThread = new Thread() {
			public void run() {
				try {
					cnChar.getInitThread().join();
					doInit();
					System.out.println("Pinyin-English init size=" + lines.size());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		initThread.start();
	}

	public List<Object[]> lines;

	private void doInit() throws IOException {
		if (lines != null)
			return;
		BufferedReader in = new BufferedReader(new InputStreamReader(U.getInstalledInputStream("encn.dict"), "utf8"));
		String line;
		lines = new ArrayList<Object[]>();
		while (true) {
			line = in.readLine();
			if (line == null)
				break;
			int p1 = line.indexOf('\t');
			if (p1 <= 0)
				continue;
			String en = line.substring(0, p1);
			String trans = line.substring(p1 + 1);
			String index = getPinyinIndex(trans);
			lines.add(new Object[] { en, trans, index, en.toLowerCase() });
		}
		in.close();
	}

	private String getPinyinIndex(String s) {
		int len = s.length();
		boolean lastIsCn = true;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < len; i++) {
			char c = s.charAt(i);
			if (c < 255) {
				if (!lastIsCn) {
					sb.append(" ");
					lastIsCn = false;
				} else {
				}
			} else {
				if (!lastIsCn) {
					sb.append(' ');
				}
				String py = cnChar.reverse(c);
				if (py != null) {
					sb.append(py);
					lastIsCn = true;
				} else {
					sb.append(' ');
					lastIsCn = false;
				}
			}
		}
		int p1 = 0;
		while (p1 < sb.length() && sb.charAt(p1) == ' ')
			p1++;
		String r = sb.substring(p1);
//		System.out.println(r);
		return r;
	}

	@Override
	public List find(String py) {
		List res = new ArrayList();
		for (Object[] row : lines) {
			String index = (String) row[2];
			int v = getValue(index, py);
			if (v > 0)
				addToList(res, v, row);
		}
		if (res.isEmpty())
			return res;
		List imeUnits = new ArrayList();
		for (Object o : res) {
			Object[] o1 = (Object[]) o;
			Object[] row = (Object[]) o1[0];
			ImeUnit u = new ImeUnit(row[0] + "\t" + row[1], py.length());
			imeUnits.add(u);
		}
//		System.out.println("imeUnits:"+imeUnits.size());
		return imeUnits;
	}

	int maxCandi = 19;

	private void addToList(List res, int v, Object[] row) {
		int p1 = 0;
		int size = res.size();
		int p = size;
		for (int i = 0; i < size; i++) {
			Object[] res1 = (Object[]) res.get(i);
			if (v > (int) res1[1]) {
				p = i;
				break;
			}
		}
		if (p < maxCandi) {
			res.add(p, new Object[] { row, v });
			while (res.size() > maxCandi) {
				res.remove(res.size() - 1);
			}
		}
	}

	private int getValue(String s, String py) {
		if (py.length() == 0) {
			return 0;
		}
		int v = 0;
		int k = s.length(), len = 0;
		final int pyl = py.length();
		final int sl = s.length() - py.length();
		for (int i = 0; i <= sl; i++) {
			int p = 0;
			while (p < pyl && py.charAt(p) == s.charAt(i + p)) {
				p++;
			}
			if (p == pyl) {
				return pack(i, p);
			}
			if (p > len) {
				len = p;
				k = i;
			}
		}
		return pack(k, len);
	}

	private int pack(int k, int p) {
		return (p << 16) | (0xffff - k);
	}

	@Override
	public Thread getInitThread() {
		return initThread;
	}

}
