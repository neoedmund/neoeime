package neoe.ime.test;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/** a test code, a input test early made */
public class P {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new P().run();
	}

	A a;

	StringBuffer imein = new StringBuffer();

	private JLabel jimein;

	private JLabel jimeselect;

	private JTextArea jta;

	protected void doAppend(String s) {
		System.out.println(s);
		jta.append(s);
	}

	private JComponent getIMEPanel() {
		final JPanel p = new JPanel();
		JPanel p2 = new JPanel();
		p2.setLayout(new BorderLayout());
		p2.add(p, BorderLayout.CENTER);
		JButton jbt1;
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
						a.find(s);
					}
				} else if (c == '-' || c == ',' || c == '[') {
					a.prev();
				} else if (c == '=' || c == '.' || c == ']') {
					a.next();
				} else if (c >= '0' && c <= '9') {
					int p = c - '0';
					if (p == 0) {
						p = 10;
					}
					p--;
					{
						String s = a.select(p);
						doAppend(s);
					}
					{
						imein.setLength(0);
						String s = imein.toString();
						jimein.setText(s);
						a.find(s);
					}
				} else if (c >= 'a' && c <= 'z') {
					imein.append(c);
					String s = imein.toString();
					jimein.setText(s);
					a.find(s);
				} else {
					System.out.println("pass " + c);
					jta.dispatchEvent(new KeyEvent(jta, KeyEvent.KEY_TYPED, 0, 0, KeyEvent.VK_UNDEFINED, c));
				}
				String v = a.out10();
				jimeselect.setText(v);
			}
		});
		p.setFocusable(true);
		p.grabFocus();
		return p2;
	}

	private void run() {
		a = new A();
		try {
			a.readPy();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		JFrame f = new JFrame();
		f.getContentPane().setLayout(new BoxLayout(f.getContentPane(), BoxLayout.PAGE_AXIS));
		f.getContentPane().add(jta = new JTextArea(4, 10));
		JComponent jimep;
		f.getContentPane().add(jimep = getIMEPanel());
		f.pack();
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jimep.grabFocus();
	}
}
