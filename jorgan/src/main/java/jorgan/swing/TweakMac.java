package jorgan.swing;

import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.swing.Action;

/**
 * Tweak appearance of jOrgan on Mac OS X.
 */
public class TweakMac {

	private static final String SHOW_GROW_BOX = "apple.awt.showGrowBox";

	private static final String USE_SCREEN_MENU_BAR = "apple.laf.useScreenMenuBar";

	private static final String ABOUT_NAME = "com.apple.mrj.application.apple.menu.about.name";

	private boolean tweaked;

	private Action preferencesAction;

	private Action aboutAction;

	private Action quitAction;

	/**
	 * Tweak the appearence for Mac OS X.
	 * 
	 * @param preferences
	 *            the action to perform for configuration
	 * @param about
	 *            the action to perform for about
	 * @param quit
	 *            the action to perform for quit
	 */
	public TweakMac(Action preferences, Action about, Action quit) {

		this.preferencesAction = preferences;
		this.aboutAction = about;
		this.quitAction = quit;

		if (isMac()) {
			try {
				Class applicationClass = Class
						.forName("com.apple.eawt.Application");
				Class applicationListenerClass = Class
						.forName("com.apple.eawt.ApplicationListener");

				Object application = applicationClass.newInstance();

				Method setEnabledPreferencesMenu = applicationClass.getMethod(
						"setEnabledPreferencesMenu",
						new Class[] { Boolean.TYPE });
				setEnabledPreferencesMenu.invoke(application,
						new Object[] { Boolean
								.valueOf(preferencesAction != null) });

				Object applicationListener = Proxy.newProxyInstance(getClass()
						.getClassLoader(),
						new Class[] { applicationListenerClass },
						new InvocationHandler() {

							public Object invoke(Object proxy, Method method,
									Object[] args) {
								Boolean handled = null;

								if ("handlePreferences"
										.equals(method.getName())
										&& preferencesAction != null) {
									perform(preferencesAction);
									handled = Boolean.TRUE;
								} else if ("handleAbout".equals(method
										.getName())
										&& aboutAction != null) {
									perform(aboutAction);
									handled = Boolean.TRUE;
								} else if ("handleQuit"
										.equals(method.getName())
										&& quitAction != null) {
									perform(quitAction);
									handled = Boolean.FALSE;
								}

								if (handled != null) {
									try {
										Class applicationEventClass = Class
												.forName("com.apple.eawt.ApplicationEvent");
										Method setHandled = applicationEventClass
												.getMethod(
														"setHandled",
														new Class[] { Boolean.TYPE });
										setHandled.invoke(args[0],
												new Object[] { handled });
									} catch (Exception notMac) {
									}
								}

								return null;
							}
						});

				Method addApplicationListener = applicationClass.getMethod(
						"addApplicationListener",
						new Class[] { applicationListenerClass });
				addApplicationListener.invoke(application,
						new Object[] { applicationListener });

				System.setProperty(SHOW_GROW_BOX, "false");
				System.setProperty(USE_SCREEN_MENU_BAR, "true");
				System.setProperty(ABOUT_NAME, "jOrgan");

				tweaked = true;
			} catch (Exception notMac) {
				tweaked = false;
			}
		}
	}

	private void perform(Action action) {
		action.actionPerformed(new ActionEvent(this,
				ActionEvent.ACTION_PERFORMED, "mac"));
	}

	/**
	 * Is the appearance tweaked for Mac.
	 * 
	 * @return <code>true</code> if tweaked
	 */
	public boolean isTweaked() {
		return tweaked;
	}

	/**
	 * Are we running on a Mac.
	 * 
	 * @return <code>true</code> if running on a Mac
	 */
	public static boolean isMac() {
		return (System.getProperty("os.name").toLowerCase().indexOf("mac") != -1);
	}
}
