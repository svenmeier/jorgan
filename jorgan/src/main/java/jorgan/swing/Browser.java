package jorgan.swing;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.logging.Logger;

public class Browser {

	private static Logger logger = Logger.getLogger(Browser.class.getName());

	private static Boolean supported;

	private static Object desktop;

	private static Method browse;

	public static boolean isSupported() {
		if (supported == null) {
			try {
				Class clazz = Class.forName("java.awt.Desktop");

				desktop = clazz.getMethod("getDesktop", new Class[0]).invoke(
						null, new Object[0]);

				browse = clazz.getMethod("browse", new Class[] { URI.class });

				supported = Boolean.TRUE;
			} catch (Exception ex) {
				logger.info("desktop not supported");
				supported = Boolean.FALSE;
			}
		}

		return supported.booleanValue();
	}

	/**
	 * Open the given uri.
	 * 
	 * @param uri
	 *            uri to show
	 * @return <code>true</code> if uri was shown
	 */
	public static boolean open(String uri) {
		try {
			return open(new URI(uri));
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * Open the given uri.
	 * 
	 * @param uri
	 *            uri to show
	 * @return <code>true</code> if uri was shown
	 */
	public static boolean open(URI uri) {
		if (isSupported()) {
			try {
				browse.invoke(desktop, new Object[] { uri });

				return true;
			} catch (Exception ex) {
			}
		}
		return false;
	}
}
