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
package bias.store;

import java.util.Collection;
import java.util.Properties;

import bias.ConfigurationException;

import junit.framework.TestCase;

/**
 * Test for {@link PropertiesStore}.
 */
public class PropertiesStoreTest extends TestCase {

	private static final String PATH = "bias/store/PropertiesStoreTest";

	private static final String KEY = PATH + "/property";

	private PropertiesStore store;

	@Override
	protected void setUp() throws Exception {
		store = new PropertiesStore(new Properties());
	}

	public void testGetKeys() throws Exception {

		store.getProperties().put(KEY, "test");

		Collection<String> keys = store.getKeys(PATH);
		assertEquals(1, keys.size());
		assertEquals(KEY, keys.iterator().next());
	}

	public void testSetValue() throws Exception {

		store.setValue(KEY, String.class, "test");

		assertEquals(store.getProperties().get(KEY), "test");
	}

	public void testGetValue() throws Exception {
		store.getProperties().put(KEY, "test");

		Object value = store.getValue(KEY, String.class);

		assertEquals("test", value);
	}
	
	public void testGetValueUnkownKey() throws Exception {
		try {
			store.getValue(KEY, String.class);
			fail();
		} catch (ConfigurationException expected) {
		}
	}
}