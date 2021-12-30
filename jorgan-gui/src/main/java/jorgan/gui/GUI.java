package jorgan.gui;

import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import bias.Configuration;
import bias.swing.MessageBox;
import jorgan.UI;
import spin.over.SpinOverEvaluator;

/**
 * Graphical user interface implementation.
 */
public class GUI implements UI {

	private static Logger log = Logger.getLogger(GUI.class.getName());

	private static Configuration config = Configuration.getRoot()
			.get(GUI.class);

	private OrganFrame frame;

	private boolean showAboutOnStartup = true;

	private Integer scale = 1;

	private LAF lookAndFeel = LAF.DEFAULT;

	public GUI() {
		config.read(this);
	}

	public boolean getShowAboutOnStart() {
		return showAboutOnStartup;
	}

	public void setShowAboutOnStartup(boolean showAboutOnStartup) {
		this.showAboutOnStartup = showAboutOnStartup;
	}

	public LAF getLookAndFeel() {
		return lookAndFeel;
	}

	public void setLookAndFeel(LAF lookAndFeel) {
		this.lookAndFeel = lookAndFeel;
	}

	/**
	 * Start the user interaction.
	 * 
	 * @param file
	 *            optional file that contains an organ
	 */
	public void display(final File file) {

		new ExceptionInit();

		new SwingInit();

		if (showAboutOnStartup) {
			AboutPanel.showSplash();
		}

		FrameInit frameInit = new FrameInit();

		AboutPanel.hideSplash();

		if (file != null) {
			new FileInit(frame, file);
		}

		frameInit.waitAndDispose();
	}

	private class SwingInit implements Runnable {

		public SwingInit() {
			if (scale > 1) {
				System.setProperty("sun.java2d.uiScale",
						Integer.toString(scale));
			}

			invokeOnSwing(this);
		}

		public void run() {
			lookAndFeel.install();

			Toolkit toolkit = Toolkit.getDefaultToolkit();
			toolkit.setDynamicLayout(true);

			try {
				Field field = toolkit.getClass()
						.getDeclaredField("awtAppClassName");
				field.setAccessible(true);
				field.set(toolkit, "jOrgan");
			} catch (Exception ex) {
				log.warning("cannot change awtAppClassName");
			}

			// IMPORTANT:
			// Never wait for the result of a spin-over or we'll
			// run into deadlocks!! (player lock <-> Swing EDT)
			SpinOverEvaluator.setDefaultWait(false);
		}
	}

	public class ExceptionInit implements Runnable, UncaughtExceptionHandler {

		public ExceptionInit() {
			Thread.setDefaultUncaughtExceptionHandler(this);

			// see #handle(Throwable)
			System.setProperty("sun.awt.exception.handler",
					getClass().getName());
		}

		public void run() {
			MessageBox box = config.get("exception")
					.read(new MessageBox(MessageBox.OPTIONS_OK_CANCEL));
			if (box.show(frame) == MessageBox.OPTION_OK) {
				System.exit(1);
			}
		}

		/**
		 * http://bugs.sun.com/view_bug.do?bug_id=4499199
		 */
		public void handle(Throwable throwable) {
			log.log(Level.SEVERE, "unexpected", throwable);

			run();
		}

		public void uncaughtException(final Thread thread,
				final Throwable throwable) {
			log.log(Level.SEVERE, "unexpected", throwable);

			invokeOnSwing(this);
		}
	}

	private class FrameInit extends WindowAdapter implements Runnable {

		public FrameInit() {
			invokeOnSwing(this);
		}

		public void run() {
			frame = new OrganFrame();
			frame.addWindowListener(this);
			frame.setVisible(true);
			frame.requestFocus();
		}

		@Override
		public synchronized void windowClosed(WindowEvent e) {
			frame = null;

			notify();
		}

		public synchronized void waitAndDispose() {
			while (frame != null) {
				try {
					wait();
				} catch (InterruptedException ex) {
					throw new Error(ex);
				}
			}
		}
	}

	private static class FileInit implements Runnable {

		private OrganFrame frame;

		private File file;

		public FileInit(OrganFrame frame, File file) {
			this.frame = frame;
			this.file = file;

			invokeOnSwing(this);
		}

		public void run() {
			frame.openOrgan(file);
		}
	}

	private static void invokeOnSwing(Runnable runnable) {
		try {
			if (SwingUtilities.isEventDispatchThread()) {
				runnable.run();
			} else {
				SwingUtilities.invokeAndWait(runnable);
			}
		} catch (InterruptedException e) {
			throw new Error(e);
		} catch (InvocationTargetException e) {
			throw new Error(e);
		}
	}
}