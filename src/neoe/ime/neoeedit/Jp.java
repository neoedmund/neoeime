package neoe.ime.neoeedit;

import java.util.ArrayList;

import neoe.ime.ImeLib;
import neoe.ime.jp.JpWordLib;
import neoe.ime.jp.JpCharLib;
import neoe.ne.Ime.ImeInterface;

public class Jp extends GeneralIme implements ImeInterface {
	public static final String NAME = "日本語";

	void initLibs() throws Exception {
		this.libs = new ArrayList();
		ImeLib kanaLib = new JpCharLib();
		ImeLib jpWordLib = new JpWordLib((JpCharLib) kanaLib);
		this.libs.add(kanaLib);
		this.libs.add(jpWordLib);
	}

	public String getImeName() {
		return "日本語";
	}
}
