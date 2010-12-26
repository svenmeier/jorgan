package jorgan.midi.mpl;

import junit.framework.TestCase;

/**
 * A test for {@link Command}.
 */
public class CommandTest extends TestCase {

	public void testNoOp() throws Exception {
		Command command = Command.fromString("");
		assertTrue(command instanceof NoOp);
		assertEquals("", command.toString());
	}

	public void testChain() throws Exception {
		Command command = Command.fromString("set 8 | set test | set test 8");
		assertTrue(command instanceof Chain);
		assertEquals(3, ((Chain) command).length());
		assertEquals("set 8 | set test | set test 8", command.toString());
	}
}