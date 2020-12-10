package neoe.ime.neoeedit;

import java.util.ArrayList;
import java.util.Collections;

import neoe.ime.ImeUnit;
import neoe.ime.cn.CnCharLib;
import neoe.ime.cnen.CnEnDict;
import neoe.ime.en.EnWord;
import neoe.ne.Ime;
import neoe.ne.Ime.ImeInterface;

public class En extends GeneralIme implements ImeInterface {

//	public En() {
//		toLowCase = false;
//	}

	public static final String NAME = "English";

	void initLibs() throws Exception {
		synchronized (initLock) {
			this.libs = new ArrayList();
			if (cnChar == null) {
				cnChar = new CnCharLib();
			}
			if (cnenDict == null) {
				cnenDict = new CnEnDict((CnCharLib) cnChar);
			}
			if (enWord == null) {
				enWord = new EnWord((CnEnDict) cnenDict);
			}
			this.libs.add(enWord);
		}
	}

	public String getImeName() {
		return "English";
	}

	@Override
	public boolean longTextMode() {
		return true;
	}

	protected void consumeYield(int index, Ime.Out param) {
		if (!this.res.isEmpty()) {
			ImeUnit unit = (ImeUnit) this.res.get(index);
			param.yield = filter(unit.txt) + " ";
			param.consumed = true;
			this.sb.setLength(0);
			param.preedit = this.sb.toString();
			if (this.sb.length() > 0) {
				this.res = find(this.sb.toString());
			} else {
				this.res = Collections.EMPTY_LIST;
			}
			this.start = 0;
		} else {
			param.yield = this.sb.toString();
			param.consumed = true;
			this.sb.setLength(0);
			this.res = Collections.EMPTY_LIST;
		}
	}

	private String filter(String txt) {
		int p1 = txt.indexOf('\t');
		if (p1 > 0) {
			return txt.substring(0, p1);
		} else {
			return txt;
		}
	}

}
