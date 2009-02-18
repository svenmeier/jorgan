package jorgan.midi.mpl;

import java.util.HashMap;

import junit.framework.TestCase;

/**
 * A test for the {@link NodeFactory}.
 */
public class NodesTest extends TestCase {

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
		Node node = new NoOp();

		assertEquals(1.0f, node.process(1.0f, context));
	}

	public void testSetValue() throws Exception {
		Node node = new Set(null, 8);

		assertEquals(8.0f, node.process(0.0f, context));
	}

	public void testSetName() throws Exception {
		Node node = new Set("test", Float.NaN);

		context.set("test", 8.0f);
		assertEquals(8.0f, node.process(0.0f, context));
	}

	public void testSetNameValue() throws Exception {
		Node node = new Set("test", 8);

		assertEquals(8.0f, node.process(0.0f, context));
	}

	public void testAddValue() throws Exception {
		Node node = new Add(null, 5);

		assertEquals(15.0f, node.process(10.0f, context));
	}

	public void testAddName() throws Exception {
		Node node = new Add("test", Float.NaN);

		context.set("test", 5.0f);
		assertEquals(15.0f, node.process(10.0f, context));
	}

	public void testSubValue() throws Exception {
		Node node = new Sub(null, 5);

		assertEquals(5.0f, node.process(10.0f, context));
	}

	public void testSubName() throws Exception {
		Node node = new Sub("test", Float.NaN);

		context.set("test", 5.0f);
		assertEquals(5.0f, node.process(10.0f, context));
	}

	public void testDivValue() throws Exception {
		Node node = new Div(null, 5);

		assertEquals(2.0f, node.process(10.0f, context));
	}

	public void testDivName() throws Exception {
		Node node = new Div("test", Float.NaN);

		context.set("test", 5.0f);
		assertEquals(2.0f, node.process(10.0f, context));
	}

	public void testMultValue() throws Exception {
		Node node = new Mult(null, 5);

		assertEquals(50.0f, node.process(10.0f, context));
	}
	
	public void testMultName() throws Exception {
		Node node = new Mult("test", Float.NaN);

		context.set("test", 5.0f);
		assertEquals(50.0f, node.process(10.0f, context));
	}
	
	public void testGet() throws Exception {
		Node node = new Get("test");

		assertEquals(0.0f, node.process(0.0f, context));
		assertEquals(0.0f, context.get("test"));
	}

	public void testEqual() throws Exception {
		Node node = new Equal(10);

		assertEquals(10.0f, node.process(10.0f, context));
	}
}
