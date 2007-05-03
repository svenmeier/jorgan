package jorgan.gui;

import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import jorgan.UI;
import spin.over.SpinOverEvaluator;
import bias.Configuration;

/**
 * Graphical UI implementation.
 */
public class GUI implements UI {

	private static Configuration config = Configuration.getRoot()
			.get(GUI.class);
	
	private FrameDisposer disposer = new FrameDisposer();

	private boolean showAboutOnStartup = true;

	private boolean useSystemLookAndFeel = true;

	public GUI() {
		config.read(this);
	}

	/**
	 * Start the user interaction.
	 * 
	 * @param file
	 *            optional file that contains an organ
	 */
	public void display(final File file) {

		if (showAboutOnStartup) {
			AboutPanel.showSplash();
		}

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				initSwing();
				showFrame(file);
			}
		});

		AboutPanel.hideSplash();

		disposer.waitForDispose();
	}

	private void showFrame(final File file) {
		OrganFrame frame = new OrganFrame();

		disposer.attach(frame);

		if (file != null) {
			frame.openOrgan(file);
		}

		frame.setVisible(true);
	}

	private void initSwing() {
		if (useSystemLookAndFeel) {
			try {
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());
			} catch (Exception ex) {
				// keep default look and feel
			}
		}

		Toolkit.getDefaultToolkit().setDynamicLayout(true);

		// IMPORTANT:
		// Never wait for the result of a spin-over or we'll
		// run into deadlocks!! (player lock <-> Swing EDT)
		SpinOverEvaluator.setDefaultWait(false);
	}

	/**
	 * The listener that gets attached to the {@link OrganFrame} to dispose it
	 * on {@link #componentHidden(ComponentEvent)}.
	 */
	private class FrameDisposer extends ComponentAdapter {

		private boolean disposed = false;

		private OrganFrame frame;

		private void attach(OrganFrame frame) {
			frame.addComponentListener(this);
			this.frame = frame;
		}

		public synchronized void componentHidden(ComponentEvent e) {
			frame.dispose();

			disposed = true;

			notify();
		}

		private synchronized void waitForDispose() {
			while (!disposed) {
				try {
					wait();
				} catch (InterruptedException ex) {
					throw new Error("unexpected interruption", ex);
				}
			}
		}
	}

	public boolean getShowAboutOnStartup() {
		return showAboutOnStartup;
	}

	public void setShowAboutOnStartup(boolean showAboutOnStartup) {
		this.showAboutOnStartup = showAboutOnStartup;
	}

	public boolean getUseSystemLookAndFeel() {
		return useSystemLookAndFeel;
	}

	public void setUseSystemLookAndFeel(boolean useSystemLookAndFeel) {
		this.useSystemLookAndFeel = useSystemLookAndFeel;
	}
}
