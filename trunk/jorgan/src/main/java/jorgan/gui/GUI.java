package jorgan.gui;

import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import jorgan.UI;
import spin.over.SpinOverEvaluator;

/**
 * Graphical UI implementation.
 */
public class GUI implements UI {

	private Object FRAME_SHOWING = new Object();

	/**
	 * Start the user interaction.
	 * 
	 * @param file
	 *            optional file that contains an organ
	 */
	public void start(final File file) {

		if (jorgan.gui.Configuration.instance().getShowAboutOnStartup()) {
			AboutPanel.showSplash();
		}

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				initSwing();
				showFrame(file);
			}
		});

		AboutPanel.hideSplash();

		synchronized (FRAME_SHOWING) {
			try {
				FRAME_SHOWING.wait();
			} catch (InterruptedException ex) {
				throw new Error("interruption on waiting for FRAME_SHOWING", ex);
			}
		}
	}

	private void showFrame(final File file) {
		final OrganFrame frame = new OrganFrame();
		frame.addComponentListener(new ComponentAdapter() {
			public void componentHidden(ComponentEvent e) {
				synchronized (FRAME_SHOWING) {
					frame.dispose();

					FRAME_SHOWING.notify();
				}
			}
		});

		if (file != null) {
			frame.openOrgan(file);
		}

		frame.setVisible(true);
	}

	private void initSwing() {
		if (Configuration.instance().getUseSystemLookAndFeel()) {
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
}
