package jorgan.midi.mpl;

import junit.framework.TestCase;

/**
 * A test for {@link Command}.
 */
public class TupleTest extends TestCase {

	public void test() throws Exception {
		Tuple tuple = Tuple.fromString("set 8, set test, set test 8");
		assertEquals(3, tuple.getLength());
		assertEquals("set 8, set test, set test 8", tuple.toString());
	}
}