package neoe.ime.spi;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.im.spi.InputMethodContext;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class LookupPanel extends JPanel implements ActionListener {

	public Font font = new Font("simsun", Font.PLAIN, 12);

	NeoeInputMethod inputMethod;

	public JButton jbt1;

	public JRadioButton jcn;

	public JLabel jimein;

	public JLabel jimeselect;

	public JRadioButton jjp;

	LookupPanel(NeoeInputMethod inputMethod, InputMethodContext context) {
		this.inputMethod = inputMethod;

		setFont(font);
		setOpaque(true);
		setForeground(Color.black);
		setBackground(Color.white);

		enableEvents(AWTEvent.KEY_EVENT_MASK);
		enableEvents(AWTEvent.MOUSE_EVENT_MASK);

		setLayout(new BorderLayout());
		add(getIMEPanel(), BorderLayout.CENTER);

	}

	public void actionPerformed(ActionEvent event) {
		if ("jbt1".equals(event.getActionCommand())) {
			String fontName = getFontName();
			try {
				System.out.println( "change font to " + fontName);
				font = new Font(fontName, Font.PLAIN, 12);
				System.out.println( "change font to " + font);
				jimein.setFont(font);
				jimeselect.setFont(font);
				jcn.setFont(font);
				jjp.setFont(font);
			} catch (Exception e) {
				System.out.println(  e);
			}

		}
	}

	private String getFontName() {
		return JOptionPane.showInputDialog("Input Font Name:");
	}

	private JComponent getIMEPanel() {
		final JPanel pmain = new JPanel();
		final JPanel pCenter = new JPanel();
		final JPanel pEast = new JPanel();
		pmain.setLayout(new BorderLayout());
		pmain.add(pCenter, BorderLayout.CENTER);
		pmain.add(pEast, BorderLayout.EAST);
		pEast.setLayout(new BoxLayout(pEast, BoxLayout.LINE_AXIS));
		pEast.add(jbt1 = new JButton(" "));
		pEast.add(jcn = new JRadioButton("拼音"));
		pEast.add(jjp = new JRadioButton("日本語"));
		ButtonGroup imeG = new ButtonGroup();
		imeG.add(jcn);
		imeG.add(jjp);
		jcn.setSelected(true);
		pCenter.setLayout(new BoxLayout(pCenter, BoxLayout.PAGE_AXIS));
		pCenter.add(jimein = new JLabel("-"));
		jimein.setFont(font);
		pCenter.add(jimeselect = new JLabel("S:1- 2- 3- 4- 5- 6- 7- 8- 9- 0- << >> ----------"));
		jimeselect.setFont(font);
		jbt1.setActionCommand("jbt1");
		jbt1.addActionListener(this);
		return pmain;
	}

	protected void processKeyEvent(KeyEvent event) {
		inputMethod.dispatchEvent(event);
	}

}