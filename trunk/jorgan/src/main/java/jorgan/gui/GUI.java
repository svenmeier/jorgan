package jorgan.gui;

import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import jorgan.UI;
import jorgan.swing.MacAdapter;
import spin.over.SpinOverEvaluator;
import bias.Configuration;

/**
 * Graphical UI implementation.
 */
public class GUI implements UI {

	public static enum LAF {
		DEFAULT, SYSTEM, CROSS_PLATFORM
	}

	private static Logger log = Logger.getLogger(GUI.class.getName());

	private static Configuration config = Configuration.getRoot()
			.get(GUI.class);

	private boolean showAboutOnStartup = true;

	private LAF lookAndFeel = LAF.DEFAULT;

	public GUI() {
		config.read(this);
	}

	public boolean getShowAboutOnStartup() {
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

		MacAdapter.getInstance().install("jOrgan");

		invokeOnSwing(new Environment());

		AboutPanel.showSplash();

		FrameWrapper wrapper = new FrameWrapper();
		invokeOnSwing(wrapper);

		if (file != null) {
			invokeOnSwing(new Opener(wrapper.getFrame(), file));
		}

		AboutPanel.hideSplash();

		wrapper.waitForEnd();
	}

	private void invokeOnSwing(Runnable runnable) {
		try {
			SwingUtilities.invokeAndWait(runnable);
		} catch (InterruptedException e) {
			throw new Error(e);
		} catch (InvocationTargetException e) {
			throw new Error(e);
		}
	}

	private class Environment implements Runnable {
		public void run() {
			String plaf = null;
			try {
				switch (lookAndFeel) {
				case DEFAULT:
					// nothing to do
					break;
				case SYSTEM:
					plaf = UIManager.getSystemLookAndFeelClassName();
					break;
				case CROSS_PLATFORM:
					plaf = UIManager.getCrossPlatformLookAndFeelClassName();
					break;
				}

				if (plaf != null) {
					log.log(Level.INFO, "setting plaf '" + plaf + "'");
					UIManager.setLookAndFeel(plaf);
				}
			} catch (Exception ex) {
				log.log(Level.WARNING, "unable to set plaf '" + plaf + "'", ex);
			}

			Toolkit.getDefaultToolkit().setDynamicLayout(true);

			// IMPORTANT:
			// Never wait for the result of a spin-over or we'll
			// run into deadlocks!! (player lock <-> Swing EDT)
			SpinOverEvaluator.setDefaultWait(false);
		}
	}

	private class FrameWrapper extends ComponentAdapter implements Runnable {

		private OrganFrame frame;

		public void run() {
			frame = new OrganFrame();
			frame.addComponentListener(this);
			frame.setVisible(true);
		}

		public OrganFrame getFrame() {
			return frame;
		}

		@Override
		public synchronized void componentHidden(ComponentEvent e) {
			frame.dispose();
			frame = null;

			notify();
		}

		public synchronized void waitForEnd() {
			while (frame != null) {
				try {
					wait();
				} catch (InterruptedException ex) {
					throw new Error(ex);
				}
			}
		}
	}

	private class Opener implements Runnable {

		private OrganFrame frame;

		private File file;

		public Opener(OrganFrame frame, File file) {
			this.frame = frame;
			this.file = file;
		}

		public void run() {
			frame.openOrgan(file);
		}
	}
}