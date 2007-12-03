package jorgan.midi.mpl;

import java.util.HashMap;

import jorgan.midi.mpl.Processor;
import jorgan.midi.mpl.Processor.Context;
import junit.framework.TestCase;

/**
 * A test for the {@link Processor}.
 */
public class ProcessorTest extends TestCase {

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

	public void testNoOp() throws Exception {
		Processor processor = new Processor("");

		assertEquals(1.0f, processor.process(1.0f, context));
	}

	public void testSetValue() throws Exception {
		Processor processor = new Processor("set 8");

		assertEquals(8.0f, processor.process(0.0f, context));
	}

	public void testSetName() throws Exception {
		Processor processor = new Processor("set test");

		context.set("test", 8.0f);
		assertEquals(8.0f, processor.process(0.0f, context));
	}

	public void testSetNameValue() throws Exception {
		Processor processor = new Processor("set test 8");

		assertEquals(8.0f, processor.process(0.0f, context));
	}

	public void testAddValue() throws Exception {
		Processor processor = new Processor("add 5");

		assertEquals(15.0f, processor.process(10.0f, context));
	}

	public void testAddName() throws Exception {
		Processor processor = new Processor("add test");

		context.set("test", 5.0f);
		assertEquals(15.0f, processor.process(10.0f, context));
	}

	public void testSubValue() throws Exception {
		Processor processor = new Processor("sub 5");

		assertEquals(5.0f, processor.process(10.0f, context));
	}

	public void testSubName() throws Exception {
		Processor processor = new Processor("sub test");

		context.set("test", 5.0f);
		assertEquals(5.0f, processor.process(10.0f, context));
	}

	public void testDivValue() throws Exception {
		Processor processor = new Processor("div 5");

		assertEquals(2.0f, processor.process(10.0f, context));
	}

	public void testDivName() throws Exception {
		Processor processor = new Processor("div test");

		context.set("test", 5.0f);
		assertEquals(2.0f, processor.process(10.0f, context));
	}

	public void testMultValue() throws Exception {
		Processor processor = new Processor("mult 5");

		assertEquals(50.0f, processor.process(10.0f, context));
	}
	
	public void testMultName() throws Exception {
		Processor processor = new Processor("mult test");

		context.set("test", 5.0f);
		assertEquals(50.0f, processor.process(10.0f, context));
	}
	
	public void testGet() throws Exception {
		Processor processor = new Processor("get test");

		assertEquals(0.0f, processor.process(0.0f, context));
		assertEquals(0.0f, context.get("test"));
	}

	public void testFilter() throws Exception {
		Processor processor = new Processor("filter 10-20");

		assertEquals(Float.NaN, processor.process(5.0f, context));
		assertEquals(15.0f, processor.process(15.0f, context));
	}
}
