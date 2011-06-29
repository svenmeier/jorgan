package jorgan.keyboard.gui.dock;

import javax.swing.JPanel;

import jorgan.disposition.Element;
import jorgan.disposition.Elements;
import jorgan.disposition.Keyboard;
import jorgan.disposition.event.OrganAdapter;
import jorgan.disposition.event.OrganListener;
import jorgan.gui.dock.AbstractView;
import jorgan.keyboard.gui.KeyboardPanel;
import jorgan.midi.mpl.ProcessingException;
import jorgan.play.KeyboardPlayer;
import jorgan.play.OrganPlay;
import jorgan.play.Player;
import jorgan.play.OrganPlay.Playing;
import jorgan.session.OrganSession;
import spin.Spin;
import bias.Configuration;

/**
 * A virtual keyboard.
 */
public class KeyboardView extends AbstractView {

	private static final Configuration config = Configuration.getRoot().get(
			KeyboardView.class);

	private OrganPlay play;

	private JPanel content;

	private Listener listener = new Listener();

	/**
	 * Constructor.
	 */
	public KeyboardView() {
		config.read(this);

		content = new JPanel(new StackLayout(4));
		setContent(content);
	}

	protected void play(Keyboard keyboard, Playing playing) {
		if (play != null) {
			play.play(keyboard, playing);
		}
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
		content.removeAll();

		if (play != null) {
			for (Keyboard keyboard : play.getOrgan()
					.getElements(Keyboard.class)) {
				content.add(createKeyboardPanel(keyboard));
			}
		}

		content.revalidate();
		content.repaint();
	}

	private KeyboardPanel createKeyboardPanel(final Keyboard keyboard) {
		KeyboardPanel panel = new KeyboardPanel() {
			@Override
			protected void onKeyPress(final int pitch, final int velocity) {
				play(keyboard, new Playing() {
					@Override
					public void play(Player<?> player) {
						((KeyboardPlayer) player).press(pitch, velocity);
					}
				});
			}

			@Override
			protected void onKeyReleased(final int pitch) {
				play(keyboard, new Playing() {
					@Override
					public void play(Player<?> player) {
						((KeyboardPlayer) player).release(pitch);
					}
				});
			}
		};

		panel.setToolTipText(Elements.getDisplayName(keyboard));

		try {
			panel.reset(keyboard.getFrom(), keyboard.getTo());
		} catch (ProcessingException noRange) {
		}

		return panel;
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