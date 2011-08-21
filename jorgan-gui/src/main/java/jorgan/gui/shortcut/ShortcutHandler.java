package jorgan.gui.shortcut;

import java.awt.KeyEventPostProcessor;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

import javax.swing.text.JTextComponent;

import jorgan.disposition.Organ;
import jorgan.disposition.Shortcut;
import jorgan.disposition.Switch;

public class ShortcutHandler {

	private Processor processor = new Processor();

	private Organ organ;

	private boolean armed;

	public ShortcutHandler(Organ organ) {
		this.organ = organ;

		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.addKeyEventPostProcessor(processor);
	}

	public void arm(boolean armed) {
		this.armed = armed;
	}

	public void destroy() {
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.removeKeyEventPostProcessor(processor);
	}

	private class Processor implements KeyEventPostProcessor {

		public boolean postProcessKeyEvent(KeyEvent e) {
			if (!armed) {
				return false;
			}

			// don't steal characters from text components
			if (e.getSource() instanceof JTextComponent) {
				return false;
			}

			if (!Shortcut.maybeShortcut(e)) {
				return false;
			}

			boolean pressed = (e.getID() == KeyEvent.KEY_PRESSED);

			for (Switch element : organ.getElements(Switch.class)) {
				Shortcut shortcut = element.getShortcut();
				if (shortcut != null && shortcut.match(e)) {
					if (pressed) {
						// note: umlauts do not trigger KeyEvent.KEY_PRESSED :(

						if (element.getDuration() == Switch.DURATION_NONE) {
							// keep active until #keyReleased()
							element.setActive(true);
						} else if (element.getDuration() == Switch.DURATION_INFINITE) {
							// always activate
							element.activate();
						} else {
							if (element.isActive()) {
								element.deactivate();
							} else {
								element.activate();
							}
						}
					} else {
						if (element.getDuration() == Switch.DURATION_NONE) {
							element.setActive(false);
						}
					}
				}
			}

			return false;
		}
	}
}