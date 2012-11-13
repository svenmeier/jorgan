package jorgan.fluidsynth.io;

import jorgan.fluidsynth.windows.Backend;
import junit.framework.TestCase;

public class BackendStreamTest extends TestCase {

	public void test() throws Exception {
		Backend backend = new BackendStream().read(getClass()
				.getResourceAsStream("backend.xml"));

		assertEquals("Direct Sound (32bit)", backend.getName());
		assertEquals("Audio ouput with Direct Sound", backend.getDescription());
		assertEquals("1.1.7", backend.getVersion());

		assertEquals(1, backend.getLinks().size());

		assertEquals(4, backend.getLibraries().size());
		assertEquals("libintl-8.dll", backend.getLibraries().get(0));
		assertEquals("libglib-2.0-0.dll", backend.getLibraries().get(1));
		assertEquals("libgthread-2.0-0.dll", backend.getLibraries().get(2));
		assertEquals("libfluidsynth.dll", backend.getLibraries().get(3));
	}
}