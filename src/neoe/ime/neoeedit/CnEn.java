package neoe.ime.neoeedit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import neoe.ime.ImeLib;
import neoe.ime.ImeUnit;
import neoe.ime.NoDupList;
import neoe.ime.cn.CnCharLib;
import neoe.ime.cnen.CnEnDict;
import neoe.ne.Ime;
import neoe.ne.Ime.ImeInterface;

public class CnEn extends GeneralIme implements ImeInterface {
	public static final String NAME = "汉英辞典";

	void initLibs() throws Exception {
		synchronized (initLock) {
			this.libs = new ArrayList();

			if (cnChar == null) {
				cnChar = new CnCharLib();
			}
			if (cnenDict == null) {
				cnenDict = new CnEnDict((CnCharLib) cnChar);
			}
			this.libs.add(cnenDict);
		}
	}

	public String getImeName() {
		return "汉英";
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

	@Override
	public boolean longTextMode() {
		return true;
	}

	public List find(String py) {
		if (!this.initStarted) {
			init();
		}
		if (!this.initFinished)
			return Collections.EMPTY_LIST;

		int len = py.length();

		if (len == 0)
			return Collections.EMPTY_LIST;
		NoDupList ndl = new NoDupList();
		py = py.toLowerCase();
		for (Object o : this.libs) {
			ImeLib lib = (ImeLib) o;
			ndl.addAll(lib.find(py));
		}
		return ndl.data;
	}
}
