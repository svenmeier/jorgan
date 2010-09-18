package jorgan.midi.mpl;

import junit.framework.TestCase;

/**
 * A test for {@link CommandFormat}.
 */
public class CommandFormatTest extends TestCase {

	private CommandFormat format = new CommandFormat();

	public void testNoOp() throws Exception {
		Command[] commands = format.parse("");
		assertTrue(commands[0] instanceof NoOp);
		assertEquals("", format.format(commands));
	}

	public void testSet() throws Exception {
		Command[] commands = format.parse("set 8, set test, set test 8");
		assertTrue(commands[0] instanceof Set);
		assertTrue(commands[1] instanceof Set);
		assertTrue(commands[2] instanceof Set);
		assertEquals("set 8, set test, set test 8", format.format(commands));
	}

	public void testAdd() throws Exception {
		Command[] commands = format.parse("add 5, add test, add test 5");
		assertTrue(commands[0] instanceof Add);
		assertTrue(commands[1] instanceof Add);
		assertTrue(commands[2] instanceof Add);
		assertEquals("add 5, add test, add test 5", format.format(commands));
	}

	public void testSub() throws Exception {
		Command[] commands = format.parse("sub 5, sub test, sub test 5");
		assertTrue(commands[0] instanceof Sub);
		assertTrue(commands[1] instanceof Sub);
		assertTrue(commands[2] instanceof Sub);
		assertEquals("sub 5, sub test, sub test 5", format.format(commands));
	}

	public void testDiv() throws Exception {
		Command[] commands = format.parse("div 5, div test, div test 5");
		assertTrue(commands[0] instanceof Div);
		assertTrue(commands[1] instanceof Div);
		assertTrue(commands[2] instanceof Div);
		assertEquals("div 5, div test, div test 5", format.format(commands));
	}

	public void testMult() throws Exception {
		Command[] commands = format.parse("mult 5, mult test, mult test 5");
		assertTrue(commands[0] instanceof Mult);
		assertTrue(commands[1] instanceof Mult);
		assertTrue(commands[2] instanceof Mult);
		assertEquals("mult 5, mult test, mult test 5", format.format(commands));
	}

	public void testGet() throws Exception {
		Command[] commands = format.parse("get test");
		assertTrue(commands[0] instanceof Get);
		assertEquals("get test", format.format(commands));
	}

	public void testEqual() throws Exception {
		Command[] commands = format.parse("equal 10");
		assertTrue(commands[0] instanceof Equal);
		assertEquals("equal 10", format.format(commands));
	}
}