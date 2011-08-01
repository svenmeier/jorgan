package jorgan.text;

import junit.framework.TestCase;

public class PadFormatTest extends TestCase {

	public void test() throws Exception {
		PadFormat format = new PadFormat(4, '0');

		assertEquals("0012", format.format(12));
	}
}
