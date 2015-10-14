package neoe.ime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class U {

	private static InputStream getFilesystemInput(String fn) throws IOException {
		return new FileInputStream(fn);
	}

	public static InputStream getInstalledInputStream(String fn) throws IOException {
		String installedFn = getMyDir() + "/" + fn;
		if (!new File(installedFn).exists()) {
			try {
				System.out.println("install " + fn);
				FileUtil.copy(getJarInputStream(fn), new FileOutputStream(installedFn));
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("install failed, read from jar");
				return getJarInputStream(fn);
			}
		}
		System.out.println("read installed file:" + installedFn);
		return getFilesystemInput(installedFn);
	}

	private static InputStream getJarInputStream(String fn) throws UnsupportedEncodingException {
		return U.class.getResourceAsStream("/" + fn);
	}

	public static String getMyDir() {
		String dir = getUserHomeDir() + "/.neoeime";
		new File(dir).mkdirs();
		return dir;
	}

	/**
	 * @return
	 */
	public static String getUserHomeDir() {
		return System.getProperty("user.home");
	}

	public static void putMultiValueMap(Map map, Object key, Object value) {
		List list = (List) map.get(key);
		if (list == null) {
			list = new ArrayList();
			map.put(key, list);
		}
		list.add(value);
	}

}
