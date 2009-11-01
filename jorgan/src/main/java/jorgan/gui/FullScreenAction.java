package jorgan.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import jorgan.disposition.Console;
import jorgan.disposition.Element;
import jorgan.disposition.event.OrganAdapter;
import jorgan.disposition.event.OrganListener;
import jorgan.session.OrganSession;
import jorgan.swing.BaseAction;
import spin.Spin;
import bias.Configuration;

/**
 * An action for initiating full screen.
 */
public class FullScreenAction extends BaseAction {

	private static Configuration config = Configuration.getRoot().get(
			FullScreenAction.class);

	private static Logger logger = Logger.getLogger(FullScreenAction.class
			.getName());

	private boolean onLoad = false;
	
	private boolean real = false;

	private Map<String, FullScreen> screens = new HashMap<String, FullScreen>();

	private OrganSession session;

	public FullScreenAction(OrganSession session, OrganFrame frame) {
		this.session = session;
		this.session.getOrgan().addOrganListener(
				(OrganListener) Spin.over(new OrganAdapter() {

					public void elementAdded(Element element) {
						if (element instanceof Console) {
							update();
						}
					}

					public void propertyChanged(Element element, String name) {
						if (element instanceof Console && "screen".equals(name)) {
							update();
						}
					}
				}));

		config.read(this);

		setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0));

		update();

		if (isEnabled() && frame.isDisplayable()) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					check();
				}
			});
		}
	}

	public void destroy() {
		leaveFullScreen();
	}

	public void setOnLoad(boolean onLoad) {
		this.onLoad = onLoad;
	}

	public void setReal(boolean real) {
		this.real = real;
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
			enterFullScreen();
		}
	}

	public void actionPerformed(ActionEvent ev) {
		if (screens.isEmpty()) {
			enterFullScreen();
		}
	}

	private void enterFullScreen() {
		for (Console console : session.getOrgan().getElements(Console.class)) {
			String screen = console.getScreen();
			if (screen == null) {
				continue;
			}

			FullScreen fullScreen;
			try {
				fullScreen = getScreen(screen);
			} catch (IllegalArgumentException ex) {
				logger.log(Level.WARNING, "full screen", ex);
				continue;
			}

			if (fullScreen != null) {
				fullScreen.addConsole(console);
			}
		}
	}

	private void leaveFullScreen() {
		for (FullScreen fullScreen : screens.values()) {
			fullScreen.dispose();
		}
		screens.clear();
	}
	
	private FullScreen getScreen(String screen) throws IllegalArgumentException {
		FullScreen fullScreen = screens.get(screen);
		if (fullScreen == null) {
			fullScreen = FullScreen.create(session, screen, real);
			fullScreen.addComponentListener(new ComponentAdapter() {
				public void componentHidden(ComponentEvent ev) {
					leaveFullScreen();
				}
			});
			screens.put(screen, fullScreen);
		}
		return fullScreen;
	}
}
