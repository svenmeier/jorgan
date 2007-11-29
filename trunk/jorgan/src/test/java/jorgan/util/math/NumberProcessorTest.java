package jorgan.util.math;

import java.util.HashMap;

import jorgan.util.math.NumberProcessor.Context;
import junit.framework.TestCase;

/**
 * A test for the {@link NumberProcessor}.
 */
public class NumberProcessorTest extends TestCase {

	private Context context;

	@Override
	protected void setUp() throws Exception {
		context = new Context() {
			private HashMap<String, Float> map = new HashMap<String, Float>();

			public float get(String name) {
				Float temp = map.get(name);
				if (temp == null) {
					return Float.NaN;
				} else {
					return temp;
				}
			}

			public void set(String name, float value) {
				map.put(name, value);
			}
		};
	}

	public void testIdentity() throws Exception {
		NumberProcessor processor = new NumberProcessor("");

		assertEquals(1.0f, processor.process(1.0f, context));
	}

	public void testConstant() throws Exception {
		NumberProcessor processor = new NumberProcessor("8");

		assertEquals(8.0f, processor.process(0.0f, context));
	}

	public void testSet() throws Exception {
		NumberProcessor processor = new NumberProcessor("set test");

		assertEquals(0.0f, processor.process(0.0f, context));
		assertEquals(0.0f, context.get("test"));
	}

	public void testGet() throws Exception {
		NumberProcessor processor = new NumberProcessor("get test");

		context.set("test", 8.0f);
		assertEquals(8.0f, processor.process(0.0f, context));
	}

	public void testGetDefault() throws Exception {
		NumberProcessor processor = new NumberProcessor("get test 1.0");

		assertEquals(1.0f, processor.process(0.0f, context));
	}

	public void testFilter() throws Exception {
		NumberProcessor processor = new NumberProcessor("filter 10-20");

		assertEquals(Float.NaN, processor.process(5.0f, context));
		assertEquals(15.0f, processor.process(15.0f, context));
	}

	public void testAdd() throws Exception {
		NumberProcessor processor = new NumberProcessor("add 5");

		assertEquals(15.0f, processor.process(10.0f, context));
	}

	public void testSub() throws Exception {
		NumberProcessor processor = new NumberProcessor("sub 5");

		assertEquals(5.0f, processor.process(10.0f, context));
	}

	public void testDiv() throws Exception {
		NumberProcessor processor = new NumberProcessor("div 5");

		assertEquals(2.0f, processor.process(10.0f, context));
	}

	public void testMult() throws Exception {
		NumberProcessor processor = new NumberProcessor("mult 5");

		assertEquals(50.0f, processor.process(10.0f, context));
	}
}
