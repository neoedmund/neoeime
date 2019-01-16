package neoe.ime.cn;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import neoe.ime.FileUtil;
import neoe.ime.U;

public class CnCharOneLib {
	static Map m;

	public CnCharOneLib() throws IOException {
		if (m == null) {
			m = init();
		}
	}

	public String getChar(String py) {
		return (String) m.get(py);
	}

	private static synchronized Map init() throws IOException {
		Map m = new HashMap<>();
		String s0 = FileUtil.readString(U.getInstalledInputStream("cn_char_one"), null);
		System.out.println(s0);
		String[] ss = s0.split("\\n");
		for (String s : ss) {
			int p1 = s.indexOf(' ');
			if (p1 > 0) {
				m.put(s.substring(0, p1).trim(), s.substring(p1 + 1).trim());
			}
		}
		// System.out.println("one=" + m);
		return m;
	}
}
