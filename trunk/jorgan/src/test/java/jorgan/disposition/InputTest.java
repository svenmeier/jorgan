package jorgan.disposition;

import junit.framework.TestCase;

public class InputTest extends TestCase {

	public void testConstant() throws Exception {
		class A extends Matcher {
			public int i;

			public float f;
		}
		A matcher = new A();
		matcher.setPattern("1,i:1,f:1");

		assertTrue(matcher.input(new int[] { 1, 1, 1 }));

		assertEquals(1, matcher.i);
		assertEquals(1.0f, matcher.f);
	}

	public void testRange() throws Exception {
		class A extends Matcher {
			public int i;

			public float f;
		}
		A matcher = new A();
		matcher.setPattern("1,i:64-127,f:64-127");

		assertTrue(matcher.input(new int[] { 1, 127, 127 }));
		assertEquals(127, matcher.i);
		assertEquals(1.0f, matcher.f);
	}

	public void testInverseRange() throws Exception {
		class A extends Matcher {
			public int i;

			public float f;
		}
		A matcher = new A();
		matcher.setPattern("1,i:127-64,f:127-64");

		assertTrue(matcher.input(new int[] { 1, 127, 127 }));
		assertEquals(127, matcher.i);
		assertEquals(-0.0f, matcher.f);
	}
}
