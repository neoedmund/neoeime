package neoe.ime.neoeedit;

import java.util.ArrayList;

import neoe.ime.ImeLib;
import neoe.ime.cn.CnCharLib;
import neoe.ime.cn.CnWordLib;
import neoe.ne.Ime.ImeInterface;

public class Pinyin extends GeneralIme implements ImeInterface {
	public static final String NAME = "拼音";

	void initLibs() throws Exception {
		this.libs = new ArrayList();
		ImeLib libaryChar;
		this.libs.add(libaryChar = new CnCharLib());
		this.libs.add(new CnWordLib((CnCharLib) libaryChar));
	}

	public String getImeName() {
		return "拼音";
	}
}
