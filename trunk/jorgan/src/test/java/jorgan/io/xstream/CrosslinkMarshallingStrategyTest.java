/*
 * jOrgan - Java Virtual Organ
 * Copyright (C) 2003 Sven Meier
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package jorgan.io.xstream;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.thoughtworks.xstream.XStream;

/**
 * Test for {@link CrossLinkMarshallingStrategy}.
 */
public class CrosslinkMarshallingStrategyTest extends TestCase {

	private XStream xstream;

	private Foo foo;

	@Override
	protected void setUp() throws Exception {
		xstream = new XStream();
		xstream.alias("foo", Foo.class);
		xstream.alias("bar", Bar.class);

		CrossLinkMarshallingStrategy strategy = new CrossLinkMarshallingStrategy();
		strategy.register(new CrossLink() {
			public boolean isCrossLinked(Object item) {
				return item instanceof Bar;
			}

			public boolean isCrossLink(Object parent, Object child) {
				return parent instanceof Bar && child instanceof Bar;
			}

			public void crossLink(Object parent, Object child) {
				if (parent instanceof Bar) {
					((Bar) parent).bar = (Bar) child;
				}
			}
		});
		xstream.setMarshallingStrategy(strategy);

		foo = new Foo();

		Bar bar1 = new Bar();
		Bar bar2 = new Bar();
		Bar bar3 = new Bar();
		Bar bar4 = new Bar();

		foo.bars.add(bar1);
		foo.bars.add(bar2);
		foo.bars.add(bar3);
		foo.bars.add(bar4);

		bar1.bar = bar2;
		bar2.bar = bar3;
		bar3.bar = bar1;
		bar4.bar = bar2;
	}

	public void testMarshal() throws Exception {
		assertEquals("<foo>\n" + "  <bars>\n" + "    <bar id=\"1\">\n"
				+ "      <bar reference=\"2\"/>\n" + "    </bar>\n"
				+ "    <bar id=\"2\">\n" + "      <bar reference=\"3\"/>\n"
				+ "    </bar>\n" + "    <bar id=\"3\">\n"
				+ "      <bar reference=\"1\"/>\n" + "    </bar>\n"
				+ "    <bar id=\"4\">\n" + "      <bar reference=\"2\"/>\n"
				+ "    </bar>\n" + "  </bars>\n" + "</foo>", xstream.toXML(foo));
	}

	public void testUnmarshal() throws Exception {
		Foo foo = (Foo) xstream.fromXML("<foo>\n" + "  <bars>\n"
				+ "    <bar id=\"1\">\n" + "      <bar reference=\"2\"/>\n"
				+ "    </bar>\n" + "    <bar id=\"2\">\n"
				+ "      <bar reference=\"3\"/>\n" + "    </bar>\n"
				+ "    <bar id=\"3\">\n" + "      <bar reference=\"1\"/>\n"
				+ "    </bar>\n" + "    <bar id=\"4\">\n"
				+ "      <bar reference=\"2\"/>\n" + "    </bar>\n"
				+ "  </bars>\n" + "</foo>");

		assertEquals(4, foo.bars.size());
		assertEquals(foo.bars.get(1), foo.bars.get(0).bar);
		assertEquals(foo.bars.get(2), foo.bars.get(1).bar);
		assertEquals(foo.bars.get(0), foo.bars.get(2).bar);
		assertEquals(foo.bars.get(1), foo.bars.get(3).bar);
	}

	static class Foo {
		public List<Bar> bars = new ArrayList<Bar>();
	}

	static class Bar {
		public Bar bar;
	}
}