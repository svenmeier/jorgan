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
import java.util.Iterator;
import java.util.List;

import jorgan.disposition.Combination;
import jorgan.disposition.Continuous;
import jorgan.disposition.Reference;
import jorgan.disposition.Switch;

public class CombinationStore {

	private Combination combination;

	private List<ReferenceStore<?>> stores = new ArrayList<ReferenceStore<?>>();

	public CombinationStore(Combination combination) {
		this.combination = combination;
	}

	public boolean isFor(Combination combination) {
		return this.combination == combination;
	}

	private ReferenceStore<?> getStore(Reference<?> reference) {
		for (ReferenceStore<?> store : stores) {
			if (store.isFor(reference.getElement())) {
				return store;
			}
		}

		ReferenceStore<?> store;
		if (reference.getElement() instanceof Switch) {
			store = new SwitchReferenceStore((Switch) reference.getElement());
		} else if (reference.getElement() instanceof Continuous) {
			store = new ContinuousReferenceStore((Continuous) reference
					.getElement());
		} else {
			throw new Error();
		}
		stores.add(store);

		return store;
	}

	public void read(Combination combination, int index) {
		for (Reference<?> reference : combination.getReferences()) {
			ReferenceStore<?> store = getStore(reference);
			store.read(combination, index);
		}
	}

	public void write(Combination combination, int index) {
		Iterator<ReferenceStore<?>> stores = this.stores.iterator();
		while (stores.hasNext()) {
			ReferenceStore<?> store = stores.next();
			try {
				store.write(combination, index);
			} catch (IllegalArgumentException unkownReference) {
				stores.remove();
			}
		}
	}
}