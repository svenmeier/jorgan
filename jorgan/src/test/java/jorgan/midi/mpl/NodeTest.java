package jorgan.midi.mpl;

import junit.framework.TestCase;

/**
 * A test for the {@link NodeFactory}.
 */
public class NodeTest extends TestCase {

	public void testNoOp() throws Exception {
		Node noOp = Node.create("");
		assertTrue(noOp instanceof NoOp);
		assertEquals("", noOp.toString());
	}

	public void testSet() throws Exception {
		Node set8 = Node.create("set 8");
		assertTrue(set8 instanceof Set);
		assertEquals("set 8.0", set8.toString());

		Node setTest = Node.create("set test");
		assertTrue(setTest instanceof Set);
		assertEquals("set test", setTest.toString());

		Node setTest8 = Node.create("set test 8");
		assertTrue(setTest8 instanceof Set);
		assertEquals("set test 8.0", setTest8.toString());
	}

	public void testAdd() throws Exception {
		Node add5 = Node.create("add 5");
		assertTrue(add5 instanceof Add);
		assertEquals("add 5.0", add5.toString());

		Node addTest = Node.create("add test");
		assertTrue(addTest instanceof Add);
		assertEquals("add test", addTest.toString());
	}

	public void testSub() throws Exception {
		Node sub5 = Node.create("sub 5");
		assertTrue(sub5 instanceof Sub);
		assertEquals("sub 5.0", sub5.toString());

		Node subTest = Node.create("sub test");
		assertTrue(subTest instanceof Sub);
		assertEquals("sub test", subTest.toString());
	}

	public void testDivValue() throws Exception {
		Node div5 = Node.create("div 5");
		assertTrue(div5 instanceof Div);
		assertEquals("div 5.0", div5.toString());

		Node divTest = Node.create("div test");
		assertTrue(divTest instanceof Div);
		assertEquals("div test", divTest.toString());
	}

	public void testMultValue() throws Exception {
		Node mult5 = Node.create("mult 5");
		assertTrue(mult5 instanceof Mult);
		assertEquals("mult 5.0", mult5.toString());

		Node multTest = Node.create("mult test");
		assertTrue(multTest instanceof Mult);
		assertEquals("mult test", multTest.toString());
	}

	public void testGet() throws Exception {
		Node getTest = Node.create("get test");
		assertTrue(getTest instanceof Get);
		assertEquals("get test", getTest.toString());
	}

	public void testEqual() throws Exception {
		Node equal10 = Node.create("equal 10");
		assertTrue(equal10 instanceof Equal);
		assertEquals("equal 10.0", equal10.toString());
	}
}