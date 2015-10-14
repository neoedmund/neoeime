package neoe.ime.fix;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import neoe.ime.Res;

public class WordsMerge {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		new WordsMerge().run();

	}

	private void readWords(String fn, Set all) throws IOException {
		long t1 = System.currentTimeMillis();
		BufferedReader in = new BufferedReader(
				new InputStreamReader(ClassLoader.getSystemResourceAsStream(fn), "utf8"));
		String line;
		int count = 0;
		while ((line = in.readLine()) != null) {
			line.trim();
			all.add(line);
			count++;
		}
		in.close();
		System.out.println("read " + fn + ":" + (System.currentTimeMillis() - t1) + " ms, " + count);
	}

	private void run() throws IOException {
		Set all = new HashSet();
		readWords(Res.CN_DICT, all);
		readWords("words2.txt", all);
		writeWords("words3.txt", all);
	}

	private void writeWords(String fn, Set all) throws IOException {
		long t1 = System.currentTimeMillis();
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fn), "utf8"));
		int count = 0;
		for (Iterator i = all.iterator(); i.hasNext();) {
			String w = (String) i.next();
			out.write(w);
			out.write("\r\n");
			count++;
		}
		out.close();
		System.out.println("write " + fn + ":" + (System.currentTimeMillis() - t1) + " ms, " + count);
	}

}
