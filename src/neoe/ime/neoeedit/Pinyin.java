package neoe.ime.neoeedit;

import java.util.ArrayList;

import neoe.ime.cn.CnCharLib;
import neoe.ime.cn.CnWordLib;
import neoe.ne.Ime.ImeInterface;

public class Pinyin extends GeneralIme implements ImeInterface {
	public static final String NAME = "拼音";

	void initLibs() throws Exception {
		synchronized (initLock) {
			this.libs = new ArrayList();
			if (cnChar == null) {
				cnChar = new CnCharLib();
			}
			if (cnWord == null) {
				cnWord = new CnWordLib((CnCharLib) cnChar);
			}
			this.libs.add(cnChar);
			this.libs.add(cnWord);
		}

	}

	public String getImeName() {
		return "拼音";
	}
	@Override
	public boolean longTextMode() {
		return false;
	}
}
