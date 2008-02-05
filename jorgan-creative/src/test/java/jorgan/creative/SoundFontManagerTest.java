package jorgan.creative;

import java.io.IOException;

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

		SoundFontManager mgr = new SoundFontManager();

		// Access it
		int num = mgr.getNumDevices();
		System.out.println("Number of Devices: " + num);

		// Loop over devices
		for (int i = 0; i < num; i++) {
			// get its name
			System.out.println("Device #" + i + ": " + mgr.getDeviceName(i));

			// loop through its banks
			for (int j = 0; j <= 127; j++) {
				if (mgr.isBankUsed(i, j)) {
					System.out.println("Bank #" + j + ": "
							+ mgr.getBankDescriptor(i, j));
				}
			}
		}

		int dev = 0, bank = 25;

		// Try to load a font
		mgr.loadBank(dev, bank,
				"c:/programme/creative/soundfonts/english organ_102.sf2");

		System.out.println("Filename in Bank #" + bank + ": "
				+ mgr.getBankFileName(dev, bank));

		// loop through its programs
		for (int p = 0; p < 128; p++) {
			try {
				System.out.println("Program #" + p + ": "
						+ mgr.getPresetDescriptor(dev, bank, p));
			} catch (IOException ex) {
				// program not used
			}
		}

		// Clear bank
		mgr.clearBank(dev, bank);
	}
}