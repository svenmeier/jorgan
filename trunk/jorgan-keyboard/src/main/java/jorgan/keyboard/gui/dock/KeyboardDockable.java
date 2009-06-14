package jorgan.keyboard.gui.dock;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;

import jorgan.gui.dock.OrganDockable;
import jorgan.keyboard.KeyboardProvider;
import jorgan.keyboard.gui.KeyboardPanel;
import jorgan.midi.Loopback;
import bias.Configuration;

/**
 * A virtual keyboard.
 */
public class KeyboardDockable extends OrganDockable {

	private static final Configuration config = Configuration.getRoot().get(
			KeyboardDockable.class);

	private KeyboardPanel keyboard = new KeyboardPanel();

	/**
	 * Constructor.
	 */
	public KeyboardDockable() {
		config.read(this);

		keyboard.setReceiver(new Receiver() {
			private Loopback loopback = KeyboardProvider.getLoopback();

			public void send(MidiMessage message, long timeStamp) {
				loopback.loopbackMessage(message, timeStamp);
			}

			public void close() {
				// ignore
			}
		});
		setContent(keyboard);
	}
}