package neoe.ime.spi;

import java.awt.AWTEvent;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.InputMethodEvent;
import java.awt.event.KeyEvent;
import java.awt.font.TextHitInfo;
import java.awt.im.spi.InputMethod;
import java.awt.im.spi.InputMethodContext;
import java.text.AttributedString;
import java.util.Locale;

import neoe.ime.Ime;
import neoe.ime.ImeImpB;

public class NeoeInputMethod implements InputMethod {

	private boolean active;

	private InputMethodContext context;

	private boolean ctrlDown;

	private Ime ime;

	private StringBuffer imein;

	private Locale locale;

	public LookupPanel lookup;

	Window lookupWindow;

	private boolean shiftDown;

	public NeoeInputMethod() {
		System.out.println("new NeoeInputMethod()");
		try {
			log("NeoeInputMethod():" + this);
			ime = new ImeImpB(this);
			imein = new StringBuffer();
		} catch (Exception e) {
			log("", e);
			e.printStackTrace();
		}
	}

	public void activate() {
		log("activate()");
		active = true;
		openLookupWindow();
	}

	private void closeLookupWindow() {
		if (lookupWindow != null) {
			lookupWindow.setVisible(false);
		}
	}

	public void deactivate(boolean isTemporary) {
		log("deactivate(" + isTemporary + ")");
		active = false;
		closeLookupWindow();
	}

	public void dispatchEvent(AWTEvent event) {
		log("dispatchEvent(" + event.getClass().getName() + ")");
		int eventType = event.getID();
		if (eventType == KeyEvent.KEY_RELEASED) {
			KeyEvent e = (KeyEvent) event;
			int keyCode = e.getKeyCode();
			if (keyCode == KeyEvent.VK_SHIFT) {
				if (shiftDown) {
					if (active) {
						deactivate(true);
					} else {
						activate();
					}
				}
			}
			if (!active) {
				return;
			}
			if (keyCode == KeyEvent.VK_CONTROL && ctrlDown && lookup != null) {
				if (lookup.jcn.isSelected()) {
					lookup.jjp.setSelected(true);
				} else {
					lookup.jcn.setSelected(true);
				}
			}
			if (keyCode == KeyEvent.VK_BACK_SPACE && active && imein.length() > 0) {
				e.consume();
			}

		} else if (active && eventType == KeyEvent.KEY_TYPED) {
			KeyEvent e = (KeyEvent) event;
			if (handleCharacter(e.getKeyChar())) {
				e.consume();
			} else {
				if (imein.length() > 0) {
					// not allow other if some char in ime buffer
					e.consume();
				}
			}
		} else if (active && eventType == KeyEvent.KEY_PRESSED) {
			KeyEvent e = (KeyEvent) event;
			int keyCode = e.getKeyCode();
			if (keyCode == KeyEvent.VK_SHIFT) {
				shiftDown = true;
			} else {
				shiftDown = false;
			}
			if (keyCode == KeyEvent.VK_CONTROL) {
				ctrlDown = true;
			} else {
				ctrlDown = false;
			}
			if (keyCode == KeyEvent.VK_BACK_SPACE && active && imein.length() > 0) {
				e.consume();
			}
		}
	}

	public void dispose() {
		log("dispose()");
		closeLookupWindow();
		// lookupWindow.dispose();
		// lookup = null;
		// lookupWindow = null;
	}

	public void endComposition() {
		log("endComposition()");
		if (imein.length() != 0) {
			String s = imein.toString();
			sendText(s, s.length());
			imein.setLength(0);
		}
		closeLookupWindow();
	}

	public Object getControlObject() {
		log("getControlObject()");
		return null;
	}

	public Locale getLocale() {
		log("getLocale()");
		return locale;
	}

	/**
	 * Attempts to handle a typed character.
	 * 
	 * @return whether the character was handled
	 */
	private boolean handleCharacter(char c) {
		log("handleCharacter(" + c + ")");
		int imecount = ime.getCount();
		boolean cn = lookup.jcn.isSelected();
		if (active) {
			if (c == '\b') {
				int len = imein.length();
				if (len > 0) {
					imein.setLength(len - 1);
					String s = imein.toString();
					lookup.jimein.setText(s);
					ime.find(s);
				}
			} else if ((cn && c == '-') || c == '[') {
				if (imecount == 0) {
					return false;
				}
				ime.prev();
			} else if ((cn && c == '=') || c == ']') {
				if (imecount == 0) {
					return false;
				}
				ime.next();
			} else if (c >= '0' && c <= '9' || c == ' ') {
				if (imecount == 0) {
					return false;
				}
				int p = c - '0';
				if (p == 0) {
					p = 10;
				}
				p--;
				if (c == ' ') {
					p = 0;
				}
				{
					int l1 = ime.getCurrentPy().length();
					String s = ime.select(p);
					int count = l1 - ime.getCurrentPy().length();
					sendText(s, count);
				}
				{
					imein.setLength(0);
					String s = ime.getCurrentPy();
					imein.append(s);
					lookup.jimein.setText(s);
					// ime.find(s);
				}
			} else if (c >= 'a' && c <= 'z' || (!cn && c == '-')) {
				imein.append(c);
				String s = imein.toString();
				lookup.jimein.setText(s);
				ime.find(s);
			} else {
				return false;
			}
			String v = ime.out();
			lookup.jbt1.setText("" + ime.getCount());
			lookup.jimeselect.setText(v);
			return true;
		}
		return false;
	}

	public void hideWindows() {
		log("hideWindows()");
		closeLookupWindow();
	}

	private void initLookupWindow() {
		try {
			lookupWindow = (Window) Const.createInputMethodJFrameMethod.invoke(context,
					new Object[] { "neoeime", Boolean.FALSE });
			lookupWindow.add(lookup = new LookupPanel(this, context));
			lookupWindow.pack();
			log("initLookupWindow():" + lookupWindow.hashCode());
		} catch (Exception e) {
			log("", e);
			e.printStackTrace();
		}
	}

	public boolean isCompositionEnabled() {
		// always enabled
		log("isCompositionEnabled()");
		return true;
	}

	private void log(String s) {
		System.out.println(s);
	}

	private void log(String string, Exception e) {
		System.out.println(string + e);
	}

	public void notifyClientWindowChange(Rectangle rect) {
		log("notifyClientWindowChange(" + rect + ")");
	}

	private void openLookupWindow() {
		if (lookupWindow == null) {
			initLookupWindow();
		}
		lookupWindow.setVisible(true);
	}

	public void reconvert() {
		log("reconvert()");
		// not supported yet
		throw new UnsupportedOperationException();
	}

	public void removeNotify() {
		log("removeNotify()");
	}

	private void sendText(String s, int count) {
		log("send " + s + "," + count);
		TextHitInfo caret = null;// caret =
		// TextHitInfo.leading(insertionPoint);
		count = s.length();
		context.dispatchInputMethodEvent(InputMethodEvent.INPUT_METHOD_TEXT_CHANGED,
				new AttributedString(s).getIterator(), count, caret, null);
	}

	public void setCharacterSubsets(Character.Subset[] subsets) {
		log("setCharacterSubsets(" + subsets + ")");
	}

	public void setCompositionEnabled(boolean enable) {
		log("setCompositionEnabled(" + enable + ")");
	}

	public void setInputMethodContext(InputMethodContext context) {
		log("setInputMethodContext(" + context + ")");
		this.context = context;
		context.enableClientWindowNotification(this, false);
	}

	public boolean setLocale(Locale locale) {
		this.locale = locale;
		return true;
	}

}
