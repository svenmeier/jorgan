package jorgan.keyboard;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.spi.MidiDeviceProvider;

import jorgan.midi.Loopback;

/**
 * The provider for the virtual keyboard.
 */
public class KeyboardProvider extends MidiDeviceProvider {

	/**
	 * The device info for this providers device.
	 */
	public static final Info INFO = new Info("jOrgan Keyboard", "jOrgan",
			"Keyboard of jOrgan", "1.0") {
	};

	private static Keyboard keyboard;

	@Override
	public MidiDevice.Info[] getDeviceInfo() {

		return new MidiDevice.Info[] { INFO };
	}

	@Override
	public MidiDevice getDevice(MidiDevice.Info info) {
		if (INFO == info) {
			return getKeyboard();
		}

		return null;
	}

	private static synchronized Keyboard getKeyboard() {
		if (keyboard == null) {
			keyboard = new Keyboard();
		}
		return keyboard;
	}

	public static void transmit(MidiMessage message, long timeStamp) {
		getKeyboard().loopOut(message);
	}

	private static class Keyboard extends Loopback {
		public Keyboard() {
			super(INFO, false, true);
		}

		@Override
		protected synchronized void loopOut(MidiMessage message) {
			super.loopOut(message);
		}
	}
}