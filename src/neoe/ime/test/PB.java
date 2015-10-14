package neoe.ime.test;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import neoe.ime.Ime;
import neoe.ime.ImeImpB;

/** a test client */
public class PB {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new PB().run();
	}

	Ime ime;

	StringBuffer imein = new StringBuffer();

	private JLabel jimein;

	private JLabel jimeselect;

	private JTextArea jta;

	JPanel keyp;

	protected void doAppend(String s) {
		// System.out.println(s);
		int pos = jta.getCaretPosition();
		jta.insert(s, pos);
		jta.setCaretPosition(pos + s.length());
		// jta.append(s);
	}

	private JComponent getIMEPanel() {
		final JPanel p = new JPanel();
		keyp = p;
		JPanel p2 = new JPanel();
		p2.setLayout(new BorderLayout());
		p2.add(p, BorderLayout.CENTER);
		final JButton jbt1;
		p2.add(jbt1 = new JButton(" "), BorderLayout.EAST);
		jbt1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				p.grabFocus();
			}
		});
		p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
		p.add(jimein = new JLabel("-"));
		Font font = new Font("simsun", 0, 12);
		jimein.setFont(font);
		p.add(jimeselect = new JLabel("S:1- 2- 3- 4- 5- 6- 7- 8- 9- 0- << >> ----------"));
		jimeselect.setFont(font);
		p.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					jta.grabFocus();
				}
			}

			public void keyReleased(KeyEvent e) {
			}

			public void keyTyped(KeyEvent e) {

				char c = e.getKeyChar();
				// System.out.println(c);
				if (c == 8) {
					int len = imein.length();
					if (len > 0) {
						imein.setLength(len - 1);
						String s = imein.toString();
						jimein.setText(s);
						ime.find(s);
					}
				} else if (c == '-' || c == '[') {
					ime.prev();
				} else if (c == '=' || c == ']') {
					ime.next();
				} else if (c >= '0' && c <= '9' || c == ' ') {
					int p = c - '0';
					if (p == 0) {
						p = 10;
					}
					p--;
					if (c == ' ') {
						p = 0;
					}
					{
						String s = ime.select(p);
						doAppend(s);
					}
					{
						imein.setLength(0);
						String s = ime.getCurrentPy();
						imein.append(s);
						jimein.setText(s);
						// ime.find(s);
					}
				} else if (c >= 'a' && c <= 'z') {
					imein.append(c);
					String s = imein.toString();
					jimein.setText(s);
					ime.find(s);
				} else {
					// System.out.println("pass "+c);
					jta.dispatchEvent(new KeyEvent(jta, KeyEvent.KEY_TYPED, 0, 0, KeyEvent.VK_UNDEFINED, c));
					return;
				}
				String v = ime.out();
				jbt1.setText("" + ime.getCount());
				jimeselect.setText(v);
			}
		});
		p.setFocusable(true);
		p.grabFocus();
		return p2;
	}

	private void run() {
		ime = new ImeImpB(null);
		JFrame f = new JFrame();
		f.getContentPane().setLayout(new BoxLayout(f.getContentPane(), BoxLayout.PAGE_AXIS));
		f.getContentPane().add(jta = new JTextArea(4, 10));
		jta.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					keyp.grabFocus();
				}
			}
		});
		// JComponent jimep;
		f.getContentPane().add(getIMEPanel());
		f.pack();
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		keyp.grabFocus();
	}
}
