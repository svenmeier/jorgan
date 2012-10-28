package jorgan;

import junit.framework.TestCase;

public class VersionTest extends TestCase {

	public void test() throws Exception {
		assertEquals("1", new Version("1").getCompatible());
		assertEquals("1.0", new Version("1.0").getCompatible());
		assertEquals("1.0", new Version("1.0.1").getCompatible());
		assertEquals("1.0", new Version("1.0-beta1").getCompatible());
	}
}
