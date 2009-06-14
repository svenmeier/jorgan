package jorgan.keyboard;

import javax.sound.midi.MidiDevice;
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

	/**
	 * The loopback.
	 */
	private static Loopback loopback;

	@Override
	public MidiDevice.Info[] getDeviceInfo() {

		return new MidiDevice.Info[] { INFO };
	}

	@Override
	public MidiDevice getDevice(MidiDevice.Info info) {
		if (INFO == info) {
			return getLoopback();
		}

		return null;
	}

	/**
	 * Get the loopback for this device.
	 * 
	 * @return the lookback
	 */
	public static synchronized Loopback getLoopback() {
		if (loopback == null) {
			loopback = new Loopback(INFO, false, true);
		}
		return loopback;
	}
}