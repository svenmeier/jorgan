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

import junit.framework.TestCase;

/**
 * Test for {@link FieldCrossLink}.
 */
public class FieldCrossLinkTest extends TestCase {

	private Bar parent;

	private Bar child;

	@Override
	protected void setUp() throws Exception {
		parent = new Bar();
		child = new Bar();
	}

	public void test() throws Exception {
		FieldCrossLink crossLink = new FieldCrossLink(Bar.class, "bar");

		assertTrue(crossLink.isLinked(parent));
		assertTrue(crossLink.isLink(parent, child));

		crossLink.link(parent, child);

		assertEquals(parent.bar, child);
	}

	static class Bar {
		public Bar bar;
	}
}