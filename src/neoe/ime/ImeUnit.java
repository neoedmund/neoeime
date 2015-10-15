package neoe.ime;

public class ImeUnit {

	/** pinyin length */
	public int pylen;

	/** word text */
	public String txt;

	public ImeUnit(String txt, int pylen) {
		this.txt = txt;
		this.pylen = pylen;
	}

	public String toString() {
		return txt + ":" + pylen;
	}

	@Override
	public int hashCode() {
		return txt.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ImeUnit))
			return false;
		ImeUnit o = (ImeUnit) obj;
		return txt.equals(o.txt);
	}
}
