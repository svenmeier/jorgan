package jorgan.io.disposition;

import jorgan.disposition.Rank;
import junit.framework.TestCase;

public class ClassMapperTest extends TestCase {

	private ClassMapper mapper;

	@Override
	protected void setUp() throws Exception {
		mapper = new ClassMapper(null);
	}

	public void testSerializedClass() {
		assertEquals("rank$engaged", mapper.serializedClass(Rank.Engaged.class));
	}

	public void testRealClass() {
		assertEquals(Rank.Engaged.class, mapper.realClass("rank$engaged"));
	}
}
