package jorgan.creative;

import java.io.File;

import jorgan.midi.DevicePool;
import jorgan.midi.Direction;
import junit.framework.TestCase;

/**
 * A simple test for our SoundFontManager
 */
public class SoundFontManagerTest extends TestCase {

	static {
		System.setProperty(SoundFontManager.JORGAN_CREATIVE_LIBRARY_PATH,
				"./target/native");
	}

	public void test() throws Exception {

		SoundFontManager manager = null;
		
		for (String deviceName : DevicePool.instance().getMidiDeviceNames(Direction.OUT)) {
			try {
				manager = new SoundFontManager(deviceName);
			} catch (IllegalArgumentException noCreativeDevice) {
			}
		}
		
		int bank = 25;

		manager.load(bank, new File("./src/main/dispositions/creative-example.SF2"));

		for (int p = 0; p < 128; p++) {
			System.out.println("Program #" + p + ": "
					+ manager.getPresetDescriptor(bank, p));
		}

		manager.clear(bank);
	}
}