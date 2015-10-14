package neoe.ime;

public interface Ime {
	/** a new find */
	void find(String py);

	int getCount();

	String getCurrentPy();

	void next();

	/** lookup info */
	String out();

	void prev();

	String select(int index);
}
