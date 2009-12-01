package jorgan.creative;

import java.io.File;

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

		// Access it
		String[] deviceNames = SoundFontManager.getDeviceNames();
		System.out.println("Number of Devices: " + deviceNames.length);

		// Loop over devices
		for (String deviceName : deviceNames) {
			// get its name
			System.out.println("Device : " + deviceName);
			// loop through its banks
			for (int j = 0; j <= 127; j++) {
				SoundFontManager manager = new SoundFontManager(deviceName, j);
				if (manager.isLoaded()) {
					System.out.println("Bank #" + j + ": "
							+ manager.getDescriptor());
				}
			}
		}

		SoundFontManager manager = new SoundFontManager(deviceNames[0], 25);

		// Try to load a font
		manager.load(new File("./src/main/dispositions/creative-example.SF2"));

		// loop through its programs
		for (int p = 0; p < 128; p++) {
			System.out.println("Program #" + p + ": "
					+ manager.getPresetDescriptor(p));
		}

		// Clear
		manager.clear();
	}
}