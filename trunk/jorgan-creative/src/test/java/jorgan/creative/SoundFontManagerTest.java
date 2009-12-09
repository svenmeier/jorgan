package jorgan.creative;

import java.io.File;
import java.io.IOException;

import jorgan.midi.DevicePool;
import jorgan.midi.Direction;
import junit.framework.TestCase;

/**
 * A simple test for {@link SoundFontManager}.
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
				break;
			} catch (IOException noCreativeDevice) {
				System.out.println(noCreativeDevice.getMessage());
			}
		}
		
		if (manager != null) {
			int bank = 25;

			if (manager.isLoaded(bank)) {
				manager.clear(bank);
			}
			manager.load(bank, new File("./src/main/dispositions/creative-example.SF2"));
			assertTrue(manager.isLoaded(bank));

			assertEquals("C:\\Dokumente und Einstellungen\\Administrator\\Desktop\\001-012-CC_Montre 8.sf2", manager.getDescriptor(bank));
			assertEquals("preset 0", manager.getPresetDescriptor(bank, 0));
			assertEquals("preset 1", manager.getPresetDescriptor(bank, 1));

			try {
				manager.getPresetDescriptor(bank, 2);
				fail();
			} catch (IllegalArgumentException invalidPreset) {
			}
			
			manager.clear(bank);
			
			manager.destroy();
		}
	}
}