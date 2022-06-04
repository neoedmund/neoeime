package neoe.ne.plugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import neoe.ne.Conf;

import neoe.ne.Ime;
import neoe.ne.Ime.ImeInterface;

public class PluginInit {

	public static void run() throws IOException {
		Map config = Conf.getConfig();
		List list = (List) config.get("ime");
		if (list == null || list.size() == 0)
			return;
		List<ImeInterface> imes = new ArrayList();
		for (Object o : list) {
			String cls = (String) o;
			try {
				Class clz = PluginInit.class.getClassLoader().loadClass(cls);
				if (!ImeInterface.class.isAssignableFrom(clz)) {
					System.out.println("IME class '" + cls + "' not implements 'ImeInterface'.");
					continue;
				}
				ImeInterface ime = null;
				try {
					imes.add(ime = (ImeInterface) clz.getConstructor().newInstance());
					System.out.println("add IME:" + ime.getImeName());
				} catch (Exception ex) {
					System.out.println("IME class '" + cls + "' cannot be inited:" + ex);
				}
			} catch (ClassNotFoundException ex) {
				System.out.println("IME class not found:" + cls);
			}
		}
		Ime.instances.addAll(imes);
	}
}
