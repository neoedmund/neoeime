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
}
