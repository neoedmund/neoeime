package neoe.ime.spi;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.im.spi.InputMethod;
import java.awt.im.spi.InputMethodDescriptor;
import java.util.Locale;

public class IMEDescriptor implements InputMethodDescriptor {

	public InputMethod createInputMethod() throws Exception {
		// seem to have multiple instance according to context
		// context change frequently on windows
		return new NeoeInputMethod();
	}

	public Locale[] getAvailableLocales() throws AWTException {
		return new Locale[] { Locale.CHINESE, Locale.JAPANESE, Locale.US };
	}

	public String getInputMethodDisplayName(Locale inputLocale, Locale displayLanguage) {
		return "NeoeIME";
	}

	public Image getInputMethodIcon(Locale inputLocale) {
		return null;
	}

	// static NeoeInputMethod ime = new NeoeInputMethod();

	public boolean hasDynamicLocaleList() {
		return false;
	}

}
