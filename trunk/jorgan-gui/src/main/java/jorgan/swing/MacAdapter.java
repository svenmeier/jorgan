package jorgan.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;

import jorgan.util.NativeUtils;

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

	/**
	 * Set the listener for files.
	 * 
	 * @param listener
	 *            the listener
	 */
	public abstract void setFileListener(ActionListener listener);

	private static class Dummy extends MacAdapter {

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

		@Override
		public void setFileListener(ActionListener listener) {
		}
	}

	private static class Real extends MacAdapter {

		private ActionListener preferencesAction;

		private ActionListener aboutAction;

		private ActionListener quitAction;

		private ActionListener fileAction;

		private Application application;

		public Real() {
			application = new Application();
			application.addApplicationListener(new ApplicationListener() {
				public void handlePreferences(ApplicationEvent ev) {
					perform(preferencesAction, "preferences");
					ev.setHandled(true);
				}

				public void handleAbout(ApplicationEvent ev) {
					perform(aboutAction, "about");
					ev.setHandled(true);
				}

				public void handleQuit(ApplicationEvent ev) {
					perform(quitAction, "quit");
					ev.setHandled(false);
				}

				public void handleOpenApplication(ApplicationEvent ev) {
				}

				public void handleOpenFile(ApplicationEvent ev) {
					String filename = ev.getFilename();
					if (filename != null) {
						perform(fileAction, filename);
						ev.setHandled(true);
					}
				}

				public void handlePrintFile(ApplicationEvent ev) {
				}

				public void handleReOpenApplication(ApplicationEvent ev) {
				};
			});
		}

		@Override
		public boolean isInstalled() {
			return true;
		}

		@Override
		public void setPreferencesListener(ActionListener action) {
			this.preferencesAction = action;

			try {
				application.setEnabledPreferencesMenu(true);
			} catch (Throwable throwable) {
				unexpected(throwable);
			}
		}

		@Override
		public void setAboutListener(ActionListener action) {
			this.aboutAction = action;

			try {
				application.setEnabledAboutMenu(true);
			} catch (Throwable throwable) {
				unexpected(throwable);
			}
		}

		@Override
		public void setQuitListener(ActionListener action) {
			this.quitAction = action;
		}

		@Override
		public void setFileListener(ActionListener action) {
			this.fileAction = action;
		}

		private void perform(ActionListener action, String command) {
			action.actionPerformed(new ActionEvent(this,
					ActionEvent.ACTION_PERFORMED, command));
		}
	}

	private static void unexpected(Throwable throwable) {
		logger.log(Level.WARNING, "unexpected failure", throwable);
	}

	/**
	 * Are we running on a Mac.
	 * 
	 * @return <code>true</code> if running on a Mac
	 */
	public static boolean isMac() {
		return NativeUtils.isMac();
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

	public static void typeToolbar(JButton button) {
		button.putClientProperty("JButton.buttonType", "toolbar");
	}

	public static void modified(JFrame frame, boolean modified) {
		frame.getRootPane().putClientProperty("Window.documentModified",
				Boolean.valueOf(modified));

	}
}