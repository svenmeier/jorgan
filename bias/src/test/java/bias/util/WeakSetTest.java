/**
 * Bias - POJO Configuration.
 * Copyright (C) 2007 Sven Meier
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package bias.util;


import java.lang.ref.WeakReference;
import java.util.Iterator;

import bias.util.WeakSet;

import junit.framework.TestCase;

/**
 * Test for {@link bias.util.WeakSet}.
 */
public class WeakSetTest extends TestCase {

	/**
	 * Test generics.
	 */
	public void testGenerics() {
		WeakSet<Object> set = new WeakSet<Object>();

		set.add("A");
		set.add("B");
		set.add("C");

		assertEquals(3, set.size());
	}

	/**
	 * Test garbage collecting.
	 * 
	 * @throws Exception
	 */
	public void testGC() throws Exception {
		WeakSet<Object> set = new WeakSet<Object>();

		Object a = new Object();
		Object b = new Object();

		set.add(a);
		set.add(b);

		assertEquals(2, set.size());

		WeakReference<Object> aRef = new WeakReference<Object>(a);
		a = null;
		assertGC("must be garbage collected", aRef);

		assertEquals(1, set.size());
	}

	private void assertGC(String message, WeakReference<Object> reference) throws Exception {

		for (int i = 0; i < 50; i++) {
			if (reference.get() == null) {
				return;
			}
			
			System.gc();
			System.gc();
			System.gc();

			synchronized (this) {
				wait(100);
			}
		}
		
		fail(message);
	}

	/**
	 * Test <code>null</code> elements.
	 */
	public void testNull() {
		WeakSet<String> set = new WeakSet<String>();

		set.add(null);
		set.add(null);

		assertEquals(1, set.size());
		
		assertTrue(set.iterator().hasNext());
	}

	/**
	 * Test iterator for <code>null</code> elements.
	 */
	public void testNullIterator() {
		WeakSet<String> set = new WeakSet<String>();

		set.add(null);

		Iterator iterator = set.iterator();

		assertTrue(iterator.hasNext());
		iterator.next();

		assertFalse(iterator.hasNext());
		try {
			iterator.next();
			fail();
		} catch (Exception expected) {
		}
	}
}
