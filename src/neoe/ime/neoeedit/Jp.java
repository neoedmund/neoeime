package neoe.ime.neoeedit;

import java.util.ArrayList;

import neoe.ime.cn.CnCharLib;
import neoe.ime.jp.JpCharLib;
import neoe.ime.jp.JpWordLib;
import neoe.ne.Ime.ImeInterface;

public class Jp extends GeneralIme implements ImeInterface {

	public static final String NAME = "日本語";

	@Override
	public void reloadDict() throws Exception {
		jpChar = null;JpCharLib.pyMap=null;
		jpWord = null;
		JpWordLib.map =null;
		initLibs();
	}

	void initLibs() throws Exception {
		synchronized (initLock) {
			this.libs = new ArrayList();
			if (jpChar == null) {
				jpChar = new JpCharLib();
			}
			if (jpWord == null) {
				jpWord = new JpWordLib((JpCharLib) jpChar);
			}
			this.libs.add(jpChar);
			this.libs.add(jpWord);
		}
	}

	public String getImeName() {
		return "日本語";
	}

	@Override
	public boolean longTextMode() {
		return false;
	}
}
