package neoe.ime.neoeedit;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.List;

import neoe.ime.ImeLib;
import neoe.ime.ImeUnit;
import neoe.ime.NoDupList;
import neoe.ne.FontList;
import neoe.ne.Ime;
import neoe.ne.Ime.ImeInterface;
import neoe.ne.Ime.Out;
import neoe.ne.U;

/**
 * work for neoeedit IME plugin format
 */
public abstract class GeneralIme implements ImeInterface {

	protected static ImeLib jpWord;
	protected static ImeLib enWord;
	protected static ImeLib cnenDict;
	protected static ImeLib jpChar;
	protected static ImeLib cnChar;
	protected static ImeLib cnWord;
	protected static Object initLock = new Object();

	protected List<ImeLib> libs;

	boolean initStarted = false;
	boolean initFinished = false;

	public void init() {
		if (this.initStarted) {
			return;
		}
		this.initStarted = true;
		try {
			initLibs();
		} catch (Exception e) {
			e.printStackTrace();
		}
		initFinished = true;
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
		String sub;
		for (int i = len; i > 0; i--) {
			sub = py.substring(0, i).toLowerCase();
			for (Object o : this.libs) {
				ImeLib lib = (ImeLib) o;
				ndl.addAll(lib.find(sub));
			}
		}
		return ndl.data;
	}

	abstract void initLibs() throws Exception;

	public void keyPressed(int keycode, Out param) {
//		if ((env.isAltDown()) || (env.isControlDown())) {
//			return;
//		}
		int kc = keycode;// env.getKeyCode();
		if ((this.sb.length() > 0) && (kc == KeyEvent.VK_ESCAPE)) {
			this.sb.setLength(0);
			param.consumed = true;
			return;
		}
		int ps = longTextMode() ? pagesize2 : pagesize1;
		if ((this.sb.length() > 0) && (this.res.size() > 0) && (kc == KeyEvent.VK_PAGE_UP)) {
			if (this.start >= ps) {
				this.start -= ps;
			}
			param.consumed = true;
		} else if ((this.sb.length() > 0) && (this.res.size() > 0) && (kc == KeyEvent.VK_PAGE_DOWN)) {
			if (this.start + ps < this.res.size()) {
				this.start += ps;
			}
			param.consumed = true;
		}
	}

	int pagesize1 = 9;
	int pagesize2 = 5;
	protected boolean toLowCase = false;

	public void keyTyped(char keyChar, Ime.Out param) {
//		if ((env.isAltDown()) || (env.isControlDown())) {
//			return;
//		}
		char c = keyChar;// env.getKeyChar();
//		System.out.println("res.size=" + res.size());
		int ps = longTextMode() ? pagesize2 : pagesize1;

		if (c == '\b') {
			int len = this.sb.length();
			if (len > 0) {
				this.sb.setLength(len - 1);
				consumePreedit(param);
			}
		} else if (Character.isDigit(c)) {
			if ((this.sb.length() == 0) || (this.res.isEmpty())) {
				return;
			}
			int index = c - '0' + this.start;
			if ((index > 0) && (index <= this.res.size())) {
				consumeYield(index - 1, param);
			}
		} else if ((c == ' ')) {
			if ((this.sb.length() > 0) && ((this.res.isEmpty()))) {
				param.yield = this.sb.toString();
				param.consumed = true;
				this.sb.setLength(0);
				return;
			}
			if ((this.sb.length() == 0) || (this.res.isEmpty())) {
				return;
			}
			consumeYield(this.start, param);
		} else if (c == '\n') {
			if ((this.sb.length() == 0)) {
				return;
			}
			param.yield = this.sb.toString();
			param.consumed = true;
			param.preedit = "";
			this.sb.setLength(0);
			res.clear();
		} else if ((c == '[') && (this.res.size() > 0)) {
			if (this.start >= ps) {
				this.start -= ps;
			}
			param.consumed = true;
		} else if ((c == ']') && (this.res.size() > 0)) {
			if (this.start + ps < this.res.size()) {
				this.start += ps;
			}
			param.consumed = true;
		} else if (isIgnoreKey(c)) {
			return;
		} else /* if (Character.isLetter(c)) */ {
			this.sb.append(c);
			consumePreedit(param);
		}
	}

	boolean isIgnoreKey(char c) {
		if (Character.isAlphabetic(c))
			return false;
		if (",.\\-".indexOf(c) >= 0)
			return false;
		return true;
	};

	StringBuffer sb = new StringBuffer();

	public void setEnabled(boolean b) {
		if (!this.initStarted) {
			new Thread() {
				public void run() {
					GeneralIme.this.init();
				}
			}.start();
		}
		this.sb.setLength(0);
	}

	public abstract String getImeName();

	public void paint(Graphics2D g1, FontList fonts, int cursorX, int cursorY, Rectangle clipBounds) {
		if ((this.res.isEmpty()) || (this.sb.length() == 0)) {
			return;
		}
		if (longTextMode()) {
			paint2(g1, fonts, cursorX, cursorY, clipBounds);
			return;
		}
		Graphics2D g2 = (Graphics2D) g1.create();
		g2.setPaintMode();
		int len = this.res.size();
		int end = this.start + pagesize1 - 1;
		if (end >= len) {
			end = len - 1;
		}
		if (this.start > end) {
			this.start = end;
		}
		int index = 1;
		int maxWidth = 0;
		int curWidth = 0;
		for (int i = this.start; i <= end; i++) {
			ImeUnit unit = (ImeUnit) this.res.get(i);
			int w = U.stringWidth(g2, fonts, index + ":" + unit.txt);
			index++;
			curWidth += w;
			if (index % 3 == 1) {
				if (curWidth > maxWidth) {
					maxWidth = curWidth;
				}
				curWidth = 0;
			}
		}
		if ((index % 3 != 1) && (curWidth > maxWidth)) {
			maxWidth = curWidth;
		}

		int lineCnt = 1 + (end - this.start) / 3;
		maxWidth += U.charWidth(g2, fonts, ' ') * 2;
		maxWidth = Math.max(maxWidth, U.stringWidth(g2, fonts, this.sb.toString()));
		maxWidth += 5;
		int lineHeight = fonts.getlineHeight();
		int height = lineHeight * (lineCnt + 1) + 5;
		Rectangle box = new Rectangle(cursorX, cursorY, maxWidth, height);
		if (cursorX + maxWidth - clipBounds.x > clipBounds.width) {
			box.x = (clipBounds.width + clipBounds.x - maxWidth);
		}
		if (cursorY + height - clipBounds.y > clipBounds.height) {
			box.y = (clipBounds.height + clipBounds.y - height);
		}
		if (box.x < 0)
			box.x = 0;
		if (box.y < 0)
			box.y = 0;
		g2.setColor(this.c0);
		g2.fill(box);
		g2.setColor(this.c1);
		int x = box.x + 2;
		int y = box.y + 2 + lineHeight;
		U.drawString(g2, fonts, this.sb.toString(), x, y);

		index = 1;
		for (int i = this.start; i <= end; i++) {
			if (index % 3 == 1) {
				y += lineHeight;
				x = box.x + 2;
			}
			ImeUnit unit = (ImeUnit) this.res.get(i);
			g2.setColor(this.c1);
			int w = U.drawString(g2, fonts, index + ":", x, y);
			x += w;
			g2.setColor(this.c2);
			x += U.drawString(g2, fonts, unit.txt + " ", x, y);
			index++;
		}
		g2.dispose();
	}

	/** one item per line, usually long */
	public void paint2(Graphics2D g1, FontList fonts, int cursorX, int cursorY, Rectangle clipBounds) {
		if ((this.res.isEmpty()) || (this.sb.length() == 0)) {
			return;
		}
		Graphics2D g2 = (Graphics2D) g1.create();
		g2.setPaintMode();
		int len = this.res.size();
		int end = this.start + pagesize2 - 1;
		if (end >= len) {
			end = len - 1;
		}
		if (this.start > end) {
			this.start = end;
		}
		int index = 1;
		int maxWidth = 0;
		int curWidth = 0;
		for (int i = this.start; i <= end; i++) {
			ImeUnit unit = (ImeUnit) this.res.get(i);
			int w = U.stringWidth(g2, fonts, index + ":" + unit.txt.replace('\t', ' '));
			index++;
			curWidth += w;
			if (curWidth > maxWidth) {
				maxWidth = curWidth;
			}
			curWidth = 0;
		}
//		final int realMaxW = 500;
//		maxWidth = Math.min(realMaxW, maxWidth);

		int lineCnt = (end - this.start) + 1;
		maxWidth += 5;
		int lineHeight = fonts.getlineHeight();
		int height = lineHeight * (lineCnt + 1) + 5;
		Rectangle box = new Rectangle(cursorX, cursorY, maxWidth, height);
		if (cursorX + maxWidth - clipBounds.x > clipBounds.width) {
			box.x = (clipBounds.width + clipBounds.x - maxWidth);
		}

		if (cursorY + height - clipBounds.y > clipBounds.height) {
			box.y = (clipBounds.height + clipBounds.y - height);
		}
		if (box.x < 0)
			box.x = 0;
		if (box.y < 0)
			box.y = 0;
		g2.setColor(this.c0);
		g2.fill(box);
		g2.setColor(this.c1);
		int x = box.x + 2;
		int y = box.y + 2 + lineHeight;
		U.drawString(g2, fonts, this.sb.toString(), x, y);
		index = 1;
		for (int i = this.start; i <= end; i++) {
			y += lineHeight;
			x = box.x + 2;
			ImeUnit unit = (ImeUnit) this.res.get(i);
			g2.setColor(this.c1);
			int w = U.drawString(g2, fonts, index + ":", x, y);
			x += w;
			g2.setColor(this.c2);
			x += U.drawString(g2, fonts, unit.txt.replace('\t', ' '), x, y);
			index++;
		}
		g2.dispose();
	}

	Color c0 = Color.decode("0xaaaaff");
	Color c1 = Color.decode("0x005500");
	Color c2 = Color.decode("0x222222");
	protected List res = Collections.EMPTY_LIST;
	int start = 0;

	protected void consumePreedit(Ime.Out param) {
		param.consumed = true;
		this.res = find(this.sb.toString());
		this.start = 0;
		if (!this.res.isEmpty()) {
			ImeUnit unit = (ImeUnit) this.res.get(0);
			param.preedit = unit.txt;
		} else {
			param.preedit = this.sb.toString();
		}
	}

	protected void consumeYield(int index, Ime.Out param) {
		if (!this.res.isEmpty()) {
			ImeUnit unit = (ImeUnit) this.res.get(index);
			param.yield = unit.txt;
			param.consumed = true;
			this.sb.delete(0, Math.min(sb.length(), unit.pylen));
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
}
