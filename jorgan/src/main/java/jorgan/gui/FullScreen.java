package jorgan.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import jorgan.disposition.Console;
import jorgan.disposition.Element;
import jorgan.disposition.Message;
import jorgan.disposition.Reference;
import jorgan.disposition.event.OrganListener;
import jorgan.session.OrganSession;
import jorgan.swing.BaseAction;
import spin.Spin;
import bias.Configuration;

/**
 * An action for initiating full screen.
 */
public class FullScreen extends BaseAction implements ComponentListener {

	private static Configuration config = Configuration.getRoot().get(
			FullScreen.class);

	private boolean onLoad = false;

	private Map<String, ConsoleDialog> dialogs = new HashMap<String, ConsoleDialog>();

	private OrganSession session;

	private OrganFrame frame;

	public FullScreen(OrganSession session, OrganFrame frame) {
		this.session = session;
		this.session.getOrgan().addOrganListener(
				(OrganListener) Spin.over(new OrganListener() {

					public void elementAdded(Element element) {
						if (element instanceof Console) {
							update();
						}
					}

					public void elementRemoved(Element element) {
					}

					public void messageAdded(Element element, Message message) {
					}

					public void messageChanged(Element element, Message message) {
					}

					public void messageRemoved(Element element, Message message) {
					}

					public void propertyChanged(Element element, String name) {
						if (element instanceof Console && "screen".equals(name)) {
							update();
						}
					}

					public void referenceAdded(Element element,
							Reference<?> reference) {
					}

					public void referenceChanged(Element element,
							Reference<?> reference) {
					}

					public void referenceRemoved(Element element,
							Reference<?> reference) {
					}
				}));

		this.frame = frame;

		config.read(this);

		frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0), this);
		frame.getRootPane().getActionMap().put(this, this);

		update();
		
		if (isEnabled() && frame.isDisplayable()) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					check();
				}
			});
		}
	}

	public void setOnLoad(boolean onLoad) {
		this.onLoad = onLoad;
	}

	public boolean getOnLoad() {
		return onLoad;
	}

	public void update() {
		for (Console console : session.getOrgan().getElements(Console.class)) {
			if (console.showFullScreen()) {
				setEnabled(true);
				return;
			}
		}
		setEnabled(false);
	}

	private void check() {
		if (onLoad) {
			goFullScreen();
		}
	}
	
	public void destroy() {
		frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
				.remove(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0));
		frame.getRootPane().getActionMap().remove(this);
	}

	public void actionPerformed(ActionEvent ev) {
		if (dialogs.isEmpty()) {
			goFullScreen();
		}
	}

	private void goFullScreen() {
		for (Console console : session.getOrgan().getElements(Console.class)) {
			String screen = console.getScreen();
			if (screen == null) {
				continue;
			}

			ConsoleDialog dialog = dialogs.get(screen);
			if (dialog == null) {
				dialog = ConsoleDialog.create(frame, session, screen);
				dialogs.put(screen, dialog);
			}
			dialog.addConsole(console);
			dialog.addComponentListener(this);
			dialog.setVisible(true);
		}
	}

	public void componentHidden(ComponentEvent e) {
		Iterator<ConsoleDialog> iterator = dialogs.values().iterator();
		while (iterator.hasNext()) {
			ConsoleDialog dialog = iterator.next();
			dialog.setVisible(false);
			dialog.dispose();
		}
		dialogs.clear();
	}

	public void componentMoved(ComponentEvent e) {
	}

	public void componentResized(ComponentEvent e) {
	}

	public void componentShown(ComponentEvent e) {
	}
}
