package jorgan.disposition;
import jorgan.disposition.Matcher;
import junit.framework.TestCase;

public class MatchTest extends TestCase {

	public void testConstant() throws Exception {
		Matcher matcher = new Matcher();
		matcher.setPattern("1,1,1");

		assertTrue(matcher.input(new int[]{1,1,1}));
		assertFalse(matcher.input(new int[]{1,1,2}));
	}
	
	public void testRange() throws Exception {
		Matcher matcher = new Matcher();
		matcher.setPattern("1,1,64-127");

		assertTrue(matcher.input(new int[]{1,1,80}));
		assertFalse(matcher.input(new int[]{1,1,40}));
	}
	
	public void testInverseRange() throws Exception {
		Matcher matcher = new Matcher();
		matcher.setPattern("1,1,127-64");

		assertTrue(matcher.input(new int[]{1,1,80}));
		assertFalse(matcher.input(new int[]{1,1,40}));
	}
}
