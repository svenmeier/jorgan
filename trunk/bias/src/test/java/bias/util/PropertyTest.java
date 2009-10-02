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

import junit.framework.TestCase;

/**
 * Test for {@link Property}.
 */
public class PropertyTest extends TestCase {

	public void testMethodProperty() throws Exception {
		Property property = new Property(MethodProperty.class, "a");

		MethodProperty a = new MethodProperty();
		assertEquals("a", property.read(a));
		assertTrue(a.getCalled);
		property.write(a, "a'");
		assertTrue(a.setCalled);
	}

	public void testPublicFieldProperty() throws Exception {
		Property property = new Property(PublicFieldProperty.class, "b");

		PublicFieldProperty b = new PublicFieldProperty();
		assertEquals("b", property.read(b));
		property.write(b, "b'");
		assertEquals("b'", b.b);
	}

	public void testPrivateFieldProperty() throws Exception {
		Property property = new Property(PrivateFieldProperty.class, "c");

		PrivateFieldProperty c = new PrivateFieldProperty();
		assertEquals("c", property.read(c));
		property.write(c, "c'");
		assertEquals("c'", c.c);
	}

	@SuppressWarnings("unused")
	public void testInheritedFieldProperty() throws Exception {
		Property property = new Property(InheritedFieldProperty.class, "c");
	}

	@SuppressWarnings("unused")
	public void testNoProperty() throws Exception {
		try {
			Property property = new Property(TestNoProperty.class, "d");
			fail();
		} catch (Exception expected) {
		}
	}

	public static class MethodProperty {
		public boolean getCalled;
		public boolean setCalled;
		
		private String a = "a";
		
		public String getA() {
			getCalled = true;
			return a;
		}

		public void setA(String a) {
			setCalled = true;
			this.a = a;
		}		
	}
	
	public static class PublicFieldProperty {
		public String b = "b";
	}
	
	public static class PrivateFieldProperty {
		private String c = "c";
	}
	
	public static class InheritedFieldProperty extends PrivateFieldProperty {
	}
	
	public static class TestNoProperty {
	}
}
