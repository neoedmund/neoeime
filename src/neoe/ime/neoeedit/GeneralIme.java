package neoe.ime.neoeedit;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import neoe.ime.ImeLib;
import neoe.ime.ImeUnit;
import neoe.ne.Ime;
import neoe.ne.Ime.ImeInterface;
import neoe.ne.Ime.Out;
import neoe.ne.U;

/**
 * work for neoeedit IME plugin format
 */
public abstract class GeneralIme implements ImeInterface {
	protected List<ImeLib> libs;
	boolean inited = false;

	public void init() {
		if (this.inited) {
			return;
		}
		try {
			this.inited = true;
			initLibs();

			System.out.println("IME init ok:" + getImeName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List find(String py) {
		if (!this.inited) {
			init();
		}

		List result = new ArrayList();
		int len = py.length();

		result.clear();
		String sub;
		for (int i = len; i > 0; i--) {
			sub = py.substring(0, i);

			for (Object o : this.libs) {
				ImeLib lib = (ImeLib) o;
				result.addAll(lib.find(sub));
			}
		}
		return result;
	}

	abstract void initLibs() throws Exception;

	public void keyPressed(KeyEvent env, Out param) {
		if ((env.isAltDown()) || (env.isControlDown())) {
			return;
		}
		int kc = env.getKeyCode();
		if ((this.sb.length() > 0) && (kc == 27)) {
			this.sb.setLength(0);
			param.consumed = true;
			return;
		}
		if ((this.sb.length() > 0) && (this.res.size() > 0) && (kc == 33)) {
			if (this.start >= 9) {
				this.start -= 9;
			}
			param.consumed = true;
		} else if ((this.sb.length() > 0) && (this.res.size() > 0) && (kc == 34)) {
			if (this.start + 9 < this.res.size()) {
				this.start += 9;
			}
			param.consumed = true;
		}
	}

	public void keyTyped(KeyEvent env, Ime.Out param) {
		if ((env.isAltDown()) || (env.isControlDown())) {
			return;
		}
		char c = env.getKeyChar();

		if (c == '\b') {
			int len = this.sb.length();
			if (len > 0) {
				this.sb.setLength(len - 1);
				consumePreedit(param);
			}
		} else if (Character.isLetter(c)) {
			this.sb.append(Character.toLowerCase(c));
			consumePreedit(param);
		} else if (Character.isDigit(c)) {
			if ((this.sb.length() == 0) || (this.res == null) || (this.res.isEmpty())) {
				return;
			}
			int index = c - '0' + this.start;
			if ((index > 0) && (index <= this.res.size())) {
				consumeYield(index - 1, param);
			}
		} else if ((c == ' ') || (c == '\n')) {
			if ((this.sb.length() > 0) && ((this.res == null) || (this.res.isEmpty()))) {
				param.yield = this.sb.toString();
				param.consumed = true;
				this.sb.setLength(0);
				return;
			}
			if ((this.sb.length() == 0) || (this.res == null) || (this.res.isEmpty())) {
				return;
			}
			consumeYield(this.start, param);
		} else if ((c == '-') && (this.res.size() > 0)) {
			if (this.start >= 9) {
				this.start -= 9;
			}
			param.consumed = true;
		} else if ((c == '=') && (this.res.size() > 0)) {
			if (this.start + 9 < this.res.size()) {
				this.start += 9;
			}
			param.consumed = true;
		}
	}

	StringBuffer sb = new StringBuffer();

	public void setEnabled(boolean b) {
		if (!this.inited) {
			new Thread() {
				public void run() {
					GeneralIme.this.init();
				}
			}.start();
		}
		this.sb.setLength(0);
	}

	public abstract String getImeName();

	public void paint(Graphics2D g1, Font[] fonts, int cursorX, int cursorY, Rectangle clipBounds) {
		if ((this.res == null) || (this.res.isEmpty()) || (this.sb.length() == 0)) {
			return;
		}
		Graphics2D g2 = (Graphics2D) g1.create();
		g2.setPaintMode();
		int len = this.res.size();
		int end = this.start + 8;
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
		int lineHeight = fonts[0].getSize();
		int height = lineHeight * (lineCnt + 1) + 5;
		Rectangle box = new Rectangle(cursorX, cursorY, maxWidth, height);
		if (cursorX + maxWidth - clipBounds.x > clipBounds.width) {
			box.x = (clipBounds.width + clipBounds.x - maxWidth);
		}
		if (cursorY + height - clipBounds.y > clipBounds.height) {
			box.y = (clipBounds.height + clipBounds.y - height);
		}
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

	Color c0 = Color.decode("0xaaaaff");
	Color c1 = Color.decode("0x005500");
	Color c2 = Color.decode("0x222222");
	List res;
	int start = 0;

	private void consumePreedit(Ime.Out param) {
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

	private void consumeYield(int index, Ime.Out param) {
		if (!this.res.isEmpty()) {
			ImeUnit unit = (ImeUnit) this.res.get(index);
			param.yield = unit.txt;
			param.consumed = true;
			this.sb.delete(0, unit.pylen);
			param.preedit = this.sb.toString();
			if (this.sb.length() > 0) {
				this.res = find(this.sb.toString());
				this.start = 0;
			}
		} else {
			param.yield = this.sb.toString();
			param.consumed = true;
			this.sb.setLength(0);
		}
	}
}
