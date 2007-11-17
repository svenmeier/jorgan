package jorgan.disposition;

import junit.framework.TestCase;

public class OutputTest extends TestCase {

	public void testConstant() throws Exception {
		@SuppressWarnings("unused")
		class A extends Matcher {
			public int i = 1;

			public float f = 1.0f;
		}

		Matcher matcher = new A();
		matcher.setPattern("1,i:1,f:1");

		int[] datas = new int[3];
		matcher.output(datas);

		assertEquals(1, datas[0]);
		assertEquals(1, datas[1]);
		assertEquals(1, datas[2]);
	}

	public void testRange() throws Exception {
		@SuppressWarnings("unused")
		class A extends Matcher {
			public int i = 62;

			public float f = 0.0f;
		}

		Matcher matcher = new A();
		matcher.setPattern("1,i:64-127,f:64-127");

		int[] datas = new int[3];
		matcher.output(datas);

		assertEquals(1, datas[0]);
		assertEquals(64, datas[1]);
		assertEquals(64, datas[2]);
	}

	public void testInverseRange() throws Exception {
		@SuppressWarnings("unused")
		class A extends Matcher {
			public int i = 62;

			public float f = 0.0f;
		}

		Matcher matcher = new A();
		matcher.setPattern("1,i:127-64,f:127-64");

		int[] datas = new int[3];
		matcher.output(datas);

		assertEquals(1, datas[0]);
		assertEquals(64, datas[1]);
		assertEquals(127, datas[2]);
	}
}
