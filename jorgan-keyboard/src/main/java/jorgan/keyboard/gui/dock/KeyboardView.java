package jorgan.keyboard.gui.dock;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;
import java.util.Collections;

import javax.swing.JComboBox;

import jorgan.disposition.Element;
import jorgan.disposition.Keyboard;
import jorgan.disposition.event.OrganAdapter;
import jorgan.disposition.event.OrganListener;
import jorgan.gui.ElementListCellRenderer;
import jorgan.gui.dock.AbstractView;
import jorgan.keyboard.gui.KeyboardPanel;
import jorgan.midi.mpl.ProcessingException;
import jorgan.play.KeyboardPlayer;
import jorgan.play.OrganPlay;
import jorgan.play.Player;
import jorgan.play.OrganPlay.Playing;
import jorgan.session.OrganSession;
import jorgan.swing.ComboBoxUtils;
import spin.Spin;
import swingx.docking.Docked;
import bias.Configuration;

/**
 * A virtual keyboard.
 */
public class KeyboardView extends AbstractView {

	private static final Configuration config = Configuration.getRoot().get(
			KeyboardView.class);

	private OrganPlay play;

	private KeyboardPanel keyboardPanel;

	private Keyboard keyboard;

	private JComboBox comboBox;

	private Listener listener = new Listener();

	/**
	 * Constructor.
	 */
	public KeyboardView() {
		config.read(this);

		keyboardPanel = new KeyboardPanel() {
			@Override
			protected void onKeyPress(final int pitch, final int velocity) {
				play(new Playing() {
					@Override
					public void play(Player<?> player) {
						((KeyboardPlayer) player).press(pitch, velocity);
					}
				});
			}

			@Override
			protected void onKeyReleased(final int pitch) {
				play(new Playing() {
					@Override
					public void play(Player<?> player) {
						((KeyboardPlayer) player).release(pitch);
					}
				});
			}
		};
		setContent(keyboardPanel);

		comboBox = new JComboBox();
		comboBox.setRenderer(new ElementListCellRenderer());
		comboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent ev) {
				keyboard = (Keyboard) comboBox.getSelectedItem();
				updateKeyboard();
			}
		});
		ComboBoxUtils.beautify(comboBox);
	}

	@Override
	public void docked(Docked docked) {
		super.docked(docked);

		docked.addTool(comboBox);
	}

	protected void play(Playing playing) {
		if (play != null) {
			if (keyboard != null) {
				play.play(keyboard, playing);
			}
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

		initKeyboard();
	}

	private void initKeyboard() {
		Collection<Keyboard> keyboards;

		if (play == null) {
			keyboards = Collections.emptyList();
			keyboard = null;
		} else {
			keyboards = play.getOrgan().getElements(Keyboard.class);

			if (keyboards.isEmpty()) {
				keyboard = null;
			} else {
				keyboard = keyboards.iterator().next();
			}
		}
		comboBox.setModel(ComboBoxUtils.createModel(keyboards));

		updateKeyboard();
	}

	private void updateKeyboard() {
		if (keyboard == null) {
			keyboardPanel.reset(0, 127);
		} else {
			int from = 0;
			int to = 127;
			try {
				from = keyboard.getFrom();
				to = keyboard.getTo();
			} catch (ProcessingException noRange) {
			}
			keyboardPanel.reset(from, to);
		}
	}

	private class Listener extends OrganAdapter {
		@Override
		public void elementAdded(Element element) {
			if (keyboard == null && element instanceof Keyboard) {
				initKeyboard();
			}
		}

		@Override
		public void elementRemoved(Element element) {
			if (keyboard == element) {
				initKeyboard();
			}
		}

		@Override
		public void propertyChanged(Element element, String name) {
			if (keyboard == element) {
				updateKeyboard();
			}
		}

		@Override
		public void indexedPropertyAdded(Element element, String name,
				Object value) {
			checkMessageChange(element, name);
		}

		@Override
		public void indexedPropertyChanged(Element element, String name,
				Object value) {
			checkMessageChange(element, name);
		}

		@Override
		public void indexedPropertyRemoved(Element element, String name,
				Object value) {
			checkMessageChange(element, name);
		}

		private void checkMessageChange(Element element, String name) {
			if (keyboard == element && Element.MESSAGE.equals(name)) {
				updateKeyboard();
			}
		}
	}
}