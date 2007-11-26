package jorgan.util.math;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 * A test for the {@link NumberProcessor}.
 */
public class NumberProcessorTest extends TestCase {

	private Map<String, Float> values;

	@Override
	protected void setUp() throws Exception {
		values = new HashMap<String, Float>();
	}

	public void testIdentity() throws Exception {
		NumberProcessor processor = new NumberProcessor("");

		assertEquals(1.0f, processor.process(1.0f, values));
	}
	
	public void testConstant() throws Exception {
		NumberProcessor processor = new NumberProcessor("8");

		assertEquals(8.0f, processor.process(0.0f, values));
	}
	
	public void testSet() throws Exception {
		NumberProcessor processor = new NumberProcessor("set:test");

		assertEquals(0.0f, processor.process(0.0f, values));
		assertEquals(0.0f, values.get("test"));
	}
	
	public void testGet() throws Exception {
		NumberProcessor processor = new NumberProcessor("get:test");

		values.put("test", 8.0f);
		assertEquals(8.0f, processor.process(0.0f, values));
	}
	
	public void testFilter() throws Exception {
		NumberProcessor processor = new NumberProcessor("filter:10-20");

		assertEquals(Float.NaN, processor.process(5.0f, values));
		assertEquals(15.0f, processor.process(15.0f, values));
	}
	
	public void testAdd() throws Exception {
		NumberProcessor processor = new NumberProcessor("add:5");

		assertEquals(15.0f, processor.process(10.0f, values));
	}
	
	public void testSub() throws Exception {
		NumberProcessor processor = new NumberProcessor("sub:5");

		assertEquals(5.0f, processor.process(10.0f, values));
	}
	
	public void testDiv() throws Exception {
		NumberProcessor processor = new NumberProcessor("div:5");

		assertEquals(2.0f, processor.process(10.0f, values));
	}
	
	public void testMult() throws Exception {
		NumberProcessor processor = new NumberProcessor("mult:5");

		assertEquals(50.0f, processor.process(10.0f, values));
	}
}
