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
package bias;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;

import junit.framework.TestCase;
import bias.store.StoreListener;

/**
 * Test for {@link bias.Configuration}s.
 */
public class ConfigurationTest extends TestCase {

	private Object property;

	private TestStore store;

	private Configuration config;

	@Override
	protected void setUp() throws Exception {
		store = new TestStore();

		config = new Configuration().get(getClass());
		config.addStore(store);
	}

	public void setProperty(Object property) {
		this.property = property;
	}

	public Object getProperty() {
		return property;
	}

	public void testHasKey() {
		assertTrue(store.hasKey(TestStore.KEY));
	}

	public void testRead() {
		config.read(this);
		assertEquals(store.value, this.property);
	}

	public void testWrite() {

		this.property = new Object();
		config.write(this);
		assertEquals(this.property, store.value);
	}

	public void testGet() {
		Configuration config1 = config.get("a/b/c");
		Configuration config2 = config.get("a").get("b").get("c");

		assertSame(config1, config2);
	}

	private static class TestStore implements Store {

		private static final String PATH = "bias/ConfigurationTest";

		private static final String KEY = PATH + "/property";

		public Object value = new Object();

		public void onError(String key, Exception ex) {
			fail();
		}

		public Set<String> getKeys(String path) throws ConfigurationException {
			assertEquals(PATH, path);
			return Collections.singleton(KEY);
		}

		public boolean hasKey(String key) {
			return KEY.equals(key);
		}

		public Object getValue(String key, Type type)
				throws ConfigurationException {
			assertEquals(KEY, key);
			return value;
		}

		public void setValue(String key, Type type, Object value)
				throws ConfigurationException {
			assertEquals(KEY, key);
			this.value = value;
		}

		public void addListener(StoreListener listener) {
		}

		public void removeListener(StoreListener listener) {
		}

		public boolean isReadOnly() {
			return false;
		}
	};
}
