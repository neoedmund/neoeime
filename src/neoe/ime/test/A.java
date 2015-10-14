package neoe.ime.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/** a test code, the first program made in the project */
public class A {
	public static void main(String[] args) throws IOException {
		new A().run();
	}

	private String cur;

	private String pydata;

	private int start = 0;

	private BufferedReader stdin;
	public String find(String a) {
		start = 0;
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

	public void next() {

		int v = start + 10;
		if (v < cur.length()) {
			start = v;
		}
		System.out.println("next " + start);
	}

	public String out10() {
		String txt = cur;
		StringBuffer sb = new StringBuffer();
		int len = Math.min(10, txt.length() - start);
		int c = 1;
		for (int i = start; len > 0; len--) {
			String s = (c == 10 ? "0" : ("" + c)) + txt.charAt(i++) + " ";
			c++;
			sb.append(s);
		}
		if (start > 0) {
			sb.append("<< ");
		}
		if (start + 10 < txt.length()) {
			sb.append(">> ");
		}
		return sb.toString();
	}

	public void prev() {
		int v = start - 10;
		if (v >= 0) {
			start = v;
		}
		System.out.println("prev " + start);
	}

	private String readLine() throws IOException {
		return stdin.readLine();
	}

	public void readPy() throws IOException {
		String fn = "py.tab.txt";
		BufferedReader in = new BufferedReader(
				new InputStreamReader(ClassLoader.getSystemResourceAsStream(fn), "utf8"));
		StringBuffer sb = new StringBuffer();
		int i;
		while ((i = in.read()) != -1) {
			sb.append((char) i);
		}
		in.close();
		pydata = sb.toString();
	}

	private void run() throws IOException {
		readPy();
		stdin = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("init ok");
		testA();
	}

	public String select(int i) {
		System.out.println(i);
		if (cur == null) {
			return "";
		}
		int p = start + i;
		System.out.println(p);
		if (p >= 0 && p < cur.length()) {
			return "" + cur.charAt(p);
		} else {
			return "";
		}
	}

	private void testA() throws IOException {
		while (true) {
			System.out.print(">");
			String a = readLine();
			String txt = find(a);
			System.out.println("found [" + txt + "]");
		}
	}

}
