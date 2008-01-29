package jorgan.fluidsynth;

import junit.framework.TestCase;

/**
 * Test for {@link Fluidsynth}.
 */
public class FluidsynthTest extends TestCase {

	static {
		System.setProperty(Fluidsynth.JORGAN_FLUIDSYNTH_LIBRARY_PATH, "./target/native");
	}
	
	public void test() throws Exception {
		Fluidsynth synth = new Fluidsynth();
		
		synth.soundFontLoad("/home/sven/Desktop/Jeux14.SF2");
		synth.programChange(0, 0);
		synth.noteOn(0, 64, 100);
		
		synchronized (this) {
			wait(5000);
		}
		
		synth.noteOff(0, 64);
		
		synth.dispose();
	}
}
