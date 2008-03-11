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
		
		if (showAboutOnStartup) {
			AboutPanel.showSplash();
		}

		Swing swing = new Swing().start(file);
		
		AboutPanel.hideSplash();
		
		swing.waitForEnd();
	}

	private class Swing extends ComponentAdapter implements Runnable {
		
		private OrganFrame frame;
		
		private File file;
		
		public Swing start(File file) {
			try {
				SwingUtilities.invokeAndWait(this);
			} catch (InterruptedException e) {
				throw new Error(e);
			} catch (InvocationTargetException e) {
				throw new Error(e);
			}
			return this;
		}

		public void run() {			
			initSwing();
			
			frame = new OrganFrame();
			frame.addComponentListener(this);

			if (file != null) {
				frame.openOrgan(file);
			}

			frame.setVisible(true);
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
		
		private void initSwing() {
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
}