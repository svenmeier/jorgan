package jorgan.keyboard.gui.dock;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;

import jorgan.gui.dock.AbstractView;
import jorgan.keyboard.KeyboardProvider;
import jorgan.keyboard.gui.KeyboardPanel;
import bias.Configuration;

/**
 * A virtual keyboard.
 */
public class KeyboardView extends AbstractView {

	private static final Configuration config = Configuration.getRoot().get(
			KeyboardView.class);

	private KeyboardPanel keyboard = new KeyboardPanel();

	/**
	 * Constructor.
	 */
	public KeyboardView() {
		config.read(this);

		keyboard.setReceiver(new Receiver() {
			public void send(MidiMessage message, long timeStamp) {
				KeyboardProvider.transmit(message, timeStamp);
			}

			public void close() {
				// ignore
			}
		});
		setContent(keyboard);
	}
}