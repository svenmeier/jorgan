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

import bias.util.WeakObject;
import junit.framework.TestCase;

/**
 * Test for {@link bias.util.WeakObject}.
 */
public class WeakObjectTest extends TestCase {

	/**
	 * Test equality of two {@link WeakObject}s to the same object.
	 */
	public void testEquality() {
		Object object = new Object();

		WeakObject<Object> weak1 = new WeakObject<Object>(object);
		WeakObject<Object> weak2 = new WeakObject<Object>(object);
		
		assertEquals(weak1, weak2);
	}
	
	/**
	 * Test non-equality of two {@link WeakObject}s to different objects.
	 */
	public void testNonEquality() {
		WeakObject<Object> weak1 = new WeakObject<Object>(new Object());
		WeakObject<Object> weak2 = new WeakObject<Object>(new Object());
		
		assertFalse(weak1.equals(weak2));
		assertFalse(weak2.equals(weak1));
	}
	
	/**
	 * Test same hashCode of two {@link WeakObject}s to the same object.
	 */
	public void testSameHashCode() {
		Object object = new Object();

		WeakObject<Object> weak1 = new WeakObject<Object>(object);
		WeakObject<Object> weak2 = new WeakObject<Object>(object);
		
		assertEquals(weak1.hashCode(), weak2.hashCode());
	}
}
