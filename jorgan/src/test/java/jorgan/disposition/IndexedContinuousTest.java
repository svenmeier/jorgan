package jorgan.disposition;

import junit.framework.TestCase;

public class IndexedContinuousTest extends TestCase {

	private IndexedContinuous continuous;

	@Override
	protected void setUp() throws Exception {
		continuous = new IndexedContinuous() {
			@Override
			public int getSize() {
				return 4;
			}
		};
		continuous.setOrgan(new Organ());
	}

	public void testGetIndex() {
		continuous.setValue(0.0f);
		assertEquals(0, continuous.getIndex());
		continuous.setValue(0.24f);
		assertEquals(0, continuous.getIndex());

		continuous.setValue(0.25f);
		assertEquals(1, continuous.getIndex());
		continuous.setValue(0.49f);
		assertEquals(1, continuous.getIndex());

		continuous.setValue(0.5f);
		assertEquals(2, continuous.getIndex());
		continuous.setValue(0.74f);
		assertEquals(2, continuous.getIndex());

		continuous.setValue(0.75f);
		assertEquals(3, continuous.getIndex());
		continuous.setValue(1.0f);
		assertEquals(3, continuous.getIndex());
	}

	public void testGetValue() {
		continuous.setIndex(0);
		assertEquals(0.125, continuous.getValue(), 0.01);

		continuous.setIndex(1);
		assertEquals(0.375, continuous.getValue(), 0.01);

		continuous.setIndex(2);
		assertEquals(0.625, continuous.getValue(), 0.01);

		continuous.setIndex(3);
		assertEquals(0.875, continuous.getValue(), 0.01);
	}
}
