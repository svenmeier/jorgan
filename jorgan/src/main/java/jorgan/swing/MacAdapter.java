package jorgan.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.apple.eawt.Application;
import com.apple.eawt.ApplicationEvent;
import com.apple.eawt.ApplicationListener;

/**
 * Adapt the application on Mac OS X.
 */
public abstract class MacAdapter {

	private static Logger logger = Logger.getLogger(MacAdapter.class.getName());

	private static MacAdapter adapter;

	/**
	 * Install the adapter for Max OS X with the given program name. <br>
	 * <strong>Invoke this method before any other method on this object.</strong>
	 * 
	 * @param name
	 *            name of program
	 */
	public abstract void install(String name);

	/**
	 * Is the adapter installed.
	 * 
	 * @return <code>true</code> if installed
	 */
	public abstract boolean isInstalled();

	/**
	 * Set the listener for preferences.
	 * 
	 * @param listener
	 *            the listener
	 */
	public abstract void setPreferencesListener(ActionListener listener);

	/**
	 * Set the listener for about.
	 * 
	 * @param listener
	 *            the listener
	 */
	public abstract void setAboutListener(ActionListener listener);

	/**
	 * Set the listener for quit.
	 * 
	 * @param listener
	 *            the listener
	 */
	public abstract void setQuitListener(ActionListener listener);

	private static class Dummy extends MacAdapter {
		@Override
		public void install(String name) {
		}

		@Override
		public boolean isInstalled() {
			return false;
		}

		@Override
		public void setPreferencesListener(ActionListener listener) {
		}

		@Override
		public void setAboutListener(ActionListener listener) {
		}

		@Override
		public void setQuitListener(ActionListener listener) {
		}
	}

	private static class Real extends MacAdapter {

		private ActionListener preferencesAction;

		private ActionListener aboutAction;

		private ActionListener quitAction;

		private Application application;

		@Override
		public void install(String name) {
			
			System.setProperty("apple.awt.showGrowBox", "false");
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty(
					"com.apple.mrj.application.apple.menu.about.name", name);

			try {
				application = new Application();
				application.addApplicationListener(new ApplicationListener() {
					public void handlePreferences(ApplicationEvent ev) {
						perform(preferencesAction);
						ev.setHandled(true);
					}

					public void handleAbout(ApplicationEvent ev) {
						perform(aboutAction);
						ev.setHandled(true);
					}

					public void handleQuit(ApplicationEvent ev) {
						perform(quitAction);
						ev.setHandled(false);
					}

					public void handleOpenApplication(ApplicationEvent ev) {
					}

					public void handleOpenFile(ApplicationEvent ev) {
					}

					public void handlePrintFile(ApplicationEvent ev) {
					}

					public void handleReOpenApplication(ApplicationEvent ev) {
					};
				});
			} catch (Throwable throwable) {
				unexpected(throwable);
			}
		}

		@Override
		public boolean isInstalled() {
			return application != null;
		}

		private void checkInstalled() {
			if (application == null) {
				throw new Error("not installed");
			}
		}

		@Override
		public void setPreferencesListener(ActionListener action) {
			checkInstalled();

			this.preferencesAction = action;

			try {
				application.setEnabledPreferencesMenu(true);
			} catch (Throwable throwable) {
				unexpected(throwable);
			}
		}

		@Override
		public void setAboutListener(ActionListener action) {
			checkInstalled();

			this.aboutAction = action;

			try {
				application.setEnabledAboutMenu(true);
			} catch (Throwable throwable) {
				unexpected(throwable);
			}
		}

		@Override
		public void setQuitListener(ActionListener action) {
			checkInstalled();

			this.quitAction = action;
		}

		private void perform(ActionListener action) {
			action.actionPerformed(new ActionEvent(this,
					ActionEvent.ACTION_PERFORMED, "mac"));
		}
	}

	private static void unexpected(Throwable throwable) {
		logger.log(Level.WARNING, "unexpected failure on adapting", throwable);
	}

	/**
	 * Are we running on a Mac.
	 * 
	 * @return <code>true</code> if running on a Mac
	 */
	public static boolean isMac() {
		return (System.getProperty("os.name").toLowerCase().indexOf("mac") != -1);
	}

	/**
	 * Get the global instance.
	 * 
	 * @return instance
	 */
	public static MacAdapter getInstance() {
		if (adapter == null) {
			if (isMac()) {
				try {
					adapter = new Real();
				} catch (Throwable throwable) {
					unexpected(throwable);

					adapter = new Dummy();
				}
			} else {
				adapter = new Dummy();
			}
		}
		return adapter;
	}
}
