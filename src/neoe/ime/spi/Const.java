package neoe.ime.spi;

import java.awt.im.spi.InputMethodContext;
import java.lang.reflect.Method;
//import java.security.AccessController;
//import java.security.PrivilegedAction;

public class Const {
	static Method createInputMethodJFrameMethod;

	static {
	/*	createInputMethodJFrameMethod = (Method) AccessController.doPrivileged(new PrivilegedAction() {
	//		public Object run() {
				try {
					return InputMethodContext.class.getMethod("createInputMethodJFrame",
							new Class[] { String.class, Boolean.TYPE });
				} catch (NoSuchMethodException e) {
				System.err.println(e);
				}
	//		}
	//	});
	*/
	}
}
