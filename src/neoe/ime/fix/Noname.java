/*
 * Created on 2005-10-22
 *
 * 
 */
package neoe.ime.fix;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Noname {

	public static void main(String[] args) throws IOException {
		long t1 = System.currentTimeMillis();
		new Noname().run();
		System.out.println("use time " + (System.currentTimeMillis() - t1) + " ms.");
		System.out.println("program end " + new Date());
	}

	/**
	 * 
	 */
	private void run() throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("abond.txt"), "utf8"));
		Set set = new HashSet();
		String line;
		while ((line = in.readLine()) != null) {
			set.add(line);
		}
		in.close();
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("abond.txt"), "UTF8"));
		for (Iterator i = set.iterator(); i.hasNext();) {
			out.write(i.next() + "\n");
		}
		out.close();
	}
}
