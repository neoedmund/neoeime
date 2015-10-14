package neoe.ime;

import java.util.List;

/** word libarary */
public interface ImeLib {

	/**
	 * @param py
	 *            pinyin, or original english char
	 * @return list of ImeUnit
	 */
	List find(String py);
	
	Thread getInitThread();

}
