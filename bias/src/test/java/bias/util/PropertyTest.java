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

import java.io.File;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

/**
 * Test for {@link Property}.
 */
public class PropertyTest extends TestCase {

	public void testMethodProperty() throws Exception {
		Property property = new Property(MethodProperty.class, "a");

		MethodProperty instance = new MethodProperty();
		assertEquals("a", property.read(instance));
		assertTrue(instance.getCalled);
		property.write(instance, "a'");
		assertTrue(instance.setCalled);
	}

	public void testPublicFieldProperty() throws Exception {
		Property property = new Property(PublicFieldProperty.class, "b");

		PublicFieldProperty instance = new PublicFieldProperty();
		assertEquals("b", property.read(instance));
		property.write(instance, "b'");
		assertEquals("b'", instance.b);
	}

	public void testPrivateFieldProperty() throws Exception {
		Property property = new Property(PrivateFieldProperty.class, "c");

		PrivateFieldProperty instance = new PrivateFieldProperty();
		assertEquals("c", property.read(instance));
		property.write(instance, "c'");
		assertEquals("c'", instance.c);
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

	@SuppressWarnings("unchecked")
	public void testGenericMethodProperty() throws Exception {
		Property property = new Property(GenericMethodProperty.class, "a");

		GenericMethodProperty instance = new GenericMethodProperty();

		List<File> a = (List<File>) property.read(instance);
		assertTrue(a.size() == 1);
		assertTrue(instance.getCalled);
		property.write(instance, Collections
				.<File> singletonList(new File("a'")));
		assertTrue(instance.a.size() == 1);
		assertTrue(instance.setCalled);
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

	public static class GenericMethodProperty {
		public boolean getCalled;
		public boolean setCalled;

		private List<File> a = Collections.<File> singletonList(new File("a"));

		public List<File> getA() {
			getCalled = true;
			return a;
		}

		public void setA(List<File> a) {
			setCalled = true;
			this.a = a;
		}
	}
}
