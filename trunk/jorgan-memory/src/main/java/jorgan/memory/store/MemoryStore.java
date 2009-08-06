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
package jorgan.memory.store;

import java.util.ArrayList;
import java.util.List;

import jorgan.disposition.Combination;
import jorgan.memory.disposition.Memory;
import jorgan.memory.store.CombinationStore;

public class MemoryStore {

	private List<String> titles = new ArrayList<String>();

	private List<CombinationStore> stores = new ArrayList<CombinationStore>();

	public MemoryStore() {
	}

	private void ensureIndex(List<?> list, int index) {
		while (list.size() <= index) {
			list.add(null);
		}
	}

	private CombinationStore getStore(Combination combination) {
		for (CombinationStore store : stores) {
			if (store.isFor(combination)) {
				return store;
			}
		}

		CombinationStore store = new CombinationStore(combination);
		stores.add(store);

		return store;
	}

	public String getTitle(int index) {
		ensureIndex(titles, index);

		String title = titles.get(index);
		if (title == null) {
			title = "";
		}
		
		return title;
	}

	public void setTitle(int index, String title) {
		ensureIndex(titles, index);

		titles.set(index, title);
	}

	public void read(Memory memory, int index) {
		for (Combination combination : memory.getReferenced(Combination.class)) {
			CombinationStore store = getStore(combination);
			if (store == null) {
				store = new CombinationStore(combination);
				stores.add(store);
			}

			store.read(combination, index);
		}
	}

	public void write(Memory memory, int index) {
		for (Combination combination : memory.getReferenced(Combination.class)) {
			CombinationStore store = getStore(combination);
			if (store == null) {
				store = new CombinationStore(combination);
				stores.add(store);
			}

			store.write(combination, index);
		}
	}
}