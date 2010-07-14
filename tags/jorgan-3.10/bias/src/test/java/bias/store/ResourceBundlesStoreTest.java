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

import junit.framework.TestCase;

/**
 * Test for {@link ResourceBundlesStore}.
 */
public class ResourceBundlesStoreTest extends TestCase {

	private static final String PATH = "bias/store/ResourceBundleStoreTest";

	private static final String KEY = PATH + "/property";

	private ResourceBundlesStore store;

	@Override
	protected void setUp() throws Exception {
		store = new ResourceBundlesStore("i18n");
	}

	public void testGetKeys() throws Exception {
		Collection<String> keys = store.getKeys(PATH);

		assertEquals(1, keys.size());
		assertEquals(KEY, keys.iterator().next());
	}

	public void testGetValue() throws Exception {
		Object value = store.getValue(KEY, String.class);

		assertEquals("ResourceBundlesStore", value);
	}
}