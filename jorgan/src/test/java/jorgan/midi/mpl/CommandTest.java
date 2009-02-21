package jorgan.midi.mpl;

import junit.framework.TestCase;

/**
 * A test for {@link Command}.
 */
public class CommandTest extends TestCase {

	public void testNoOp() throws Exception {
		Command noOp = Command.create("");
		assertTrue(noOp instanceof NoOp);
		assertEquals("", noOp.toString());
	}

	public void testSet() throws Exception {
		Command set8 = Command.create("set 8");
		assertTrue(set8 instanceof Set);
		assertEquals("set 8", set8.toString());

		Command setTest = Command.create("set test");
		assertTrue(setTest instanceof Set);
		assertEquals("set test", setTest.toString());

		Command setTest8 = Command.create("set test 8");
		assertTrue(setTest8 instanceof Set);
		assertEquals("set test 8", setTest8.toString());
	}

	public void testAdd() throws Exception {
		Command add5 = Command.create("add 5");
		assertTrue(add5 instanceof Add);
		assertEquals("add 5", add5.toString());

		Command addTest = Command.create("add test");
		assertTrue(addTest instanceof Add);
		assertEquals("add test", addTest.toString());
	}

	public void testSub() throws Exception {
		Command sub5 = Command.create("sub 5");
		assertTrue(sub5 instanceof Sub);
		assertEquals("sub 5", sub5.toString());

		Command subTest = Command.create("sub test");
		assertTrue(subTest instanceof Sub);
		assertEquals("sub test", subTest.toString());
	}

	public void testDivValue() throws Exception {
		Command div5 = Command.create("div 5");
		assertTrue(div5 instanceof Div);
		assertEquals("div 5", div5.toString());

		Command divTest = Command.create("div test");
		assertTrue(divTest instanceof Div);
		assertEquals("div test", divTest.toString());
	}

	public void testMultValue() throws Exception {
		Command mult5 = Command.create("mult 5");
		assertTrue(mult5 instanceof Mult);
		assertEquals("mult 5", mult5.toString());

		Command multTest = Command.create("mult test");
		assertTrue(multTest instanceof Mult);
		assertEquals("mult test", multTest.toString());
	}

	public void testGet() throws Exception {
		Command getTest = Command.create("get test");
		assertTrue(getTest instanceof Get);
		assertEquals("get test", getTest.toString());
	}

	public void testEqual() throws Exception {
		Command equal10 = Command.create("equal 10");
		assertTrue(equal10 instanceof Equal);
		assertEquals("equal 10", equal10.toString());
	}
}