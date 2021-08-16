package jorgan.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;

import com.apple.eawt.AboutHandler;
import com.apple.eawt.AppEvent.AboutEvent;
import com.apple.eawt.AppEvent.OpenFilesEvent;
import com.apple.eawt.AppEvent.PreferencesEvent;
import com.apple.eawt.AppEvent.QuitEvent;
import com.apple.eawt.Application;
import com.apple.eawt.OpenFilesHandler;
import com.apple.eawt.PreferencesHandler;
import com.apple.eawt.QuitHandler;
import com.apple.eawt.QuitResponse;

import jorgan.util.NativeUtils;

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
	public void setPreferencesListener(ActionListener listener) {
	}

	/**
	 * Set the listener for about.
	 * 
	 * @param listener
	 *            the listener
	 */
	public void setAboutListener(ActionListener listener) {
	}

	/**
	 * Set the listener for quit.
	 * 
	 * @param listener
	 *            the listener
	 */
	public void setQuitListener(ActionListener listener) {
	}

	/**
	 * Set the listener for files.
	 * 
	 * @param listener
	 *            the listener
	 */
	public void setFileListener(ActionListener listener) {
	}

	private static class Dummy extends MacAdapter {

		@Override
		public boolean isInstalled() {
			return false;
		}
	}

	private static class Real extends MacAdapter {

		private Application application;

		public Real() {
			application = Application.getApplication();
		}

		@Override
		public boolean isInstalled() {
			return true;
		}

		@Override
		public void setPreferencesListener(ActionListener action) {
			application.setPreferencesHandler(new PreferencesHandler() {
				@Override
				public void handlePreferences(PreferencesEvent ev) {
					perform(action, "preferences");
				}
			});
		}

		@Override
		public void setAboutListener(ActionListener action) {
			application.setAboutHandler(new AboutHandler() {
				@Override
				public void handleAbout(AboutEvent ev) {
					perform(action, "about");
				}
			});
		}

		@Override
		public void setQuitListener(ActionListener action) {
			application.setQuitHandler(new QuitHandler() {
				@Override
				public void handleQuitRequestWith(QuitEvent arg0,
						QuitResponse arg1) {
					perform(action, "quit");
				}
			});
		}

		@Override
		public void setFileListener(ActionListener action) {
			application.setOpenFileHandler(new OpenFilesHandler() {
				@Override
				public void openFiles(OpenFilesEvent ev) {
					List<File> files = ev.getFiles();
					if (files != null && files.size() == 1) {
						perform(action, files.get(0).toString());
					}
				}
			});
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