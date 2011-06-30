package jorgan.keyboard.gui.dock;

import javax.swing.JScrollPane;

import jorgan.disposition.Element;
import jorgan.disposition.Keyboard;
import jorgan.disposition.event.OrganAdapter;
import jorgan.disposition.event.OrganListener;
import jorgan.gui.dock.AbstractView;
import jorgan.keyboard.gui.KeyboardsPanel;
import jorgan.play.OrganPlay;
import jorgan.session.OrganSession;
import spin.Spin;
import bias.Configuration;

public class KeyboardView extends AbstractView {

	private static final Configuration config = Configuration.getRoot().get(
			KeyboardView.class);

	private OrganPlay play;

	private Listener listener = new Listener();

	private KeyboardsPanel keyboardsPanel;

	/**
	 * Constructor.
	 */
	public KeyboardView() {
		config.read(this);
	}

	@Override
	public void setSession(OrganSession session) {
		if (play != null) {
			play.getOrgan().removeOrganListener(
					(OrganListener) Spin.over(listener));
			play = null;
		}

		if (session != null) {
			play = session.lookup(OrganPlay.class);
			play.getOrgan().addOrganListener(
					(OrganListener) Spin.over(listener));
		}

		initKeyboards();
	}

	private void initKeyboards() {
		if (keyboardsPanel != null) {
			keyboardsPanel.destroy();
			keyboardsPanel = null;

			setContent(null);
		}

		if (play != null) {
			keyboardsPanel = new KeyboardsPanel(play);

			setContent(new JScrollPane(keyboardsPanel));
		}
	}

	private class Listener extends OrganAdapter {
		@Override
		public void elementAdded(Element element) {
			checkKeyboard(element);
		}

		@Override
		public void elementRemoved(Element element) {
			checkKeyboard(element);
		}

		@Override
		public void propertyChanged(Element element, String name) {
			checkKeyboard(element);
		}

		@Override
		public void indexedPropertyAdded(Element element, String name,
				Object value) {
			checkKeyboard(element);
		}

		@Override
		public void indexedPropertyChanged(Element element, String name,
				Object value) {
			checkKeyboard(element);
		}

		@Override
		public void indexedPropertyRemoved(Element element, String name,
				Object value) {
			checkKeyboard(element);
		}

		private void checkKeyboard(Element element) {
			if (element instanceof Keyboard) {
				initKeyboards();
			}
		}
	}
}