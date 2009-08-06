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
package jorgan.memory;

import java.util.ArrayList;
import java.util.List;

import jorgan.disposition.Combination;
import jorgan.disposition.Element;
import jorgan.disposition.Organ;
import jorgan.disposition.Reference;
import jorgan.disposition.event.OrganAdapter;
import jorgan.memory.disposition.Memory;
import jorgan.memory.store.MemoryStore;

public class Store {

	private List<StoreListener> listeners = new ArrayList<StoreListener>();

	private MemoryStore memoryStore = new MemoryStore();

	private Organ organ;

	public Store(Organ organ) {
		this.organ = organ;

		organ.addOrganListener(new OrganAdapter() {
			@Override
			public void propertyChanged(Element element, String name) {
				if (element instanceof Memory) {
					if ("value".equals(name)) {
						write();

						fireIndexChanged();
					} else if ("size".equals(name)) {
						fireChanged();
					}
				}
			}

			@Override
			public void referenceAdded(Element element, Reference<?> reference) {
				if (element instanceof Combination) {
					Memory memory = memory();
					if (memory != null && memory.references(element)) {
						read();
					}
				}
			}

			@Override
			public void referenceChanged(Element element, Reference<?> reference) {
				if (element instanceof Combination) {
					Memory memory = memory();
					if (memory != null && memory.references(element)) {
						read();
					}
				}
			}

			@Override
			public void elementAdded(Element element) {
				if (element instanceof Memory) {
					fireChanged();
				}
			}

			@Override
			public void elementRemoved(Element element) {
				if (element instanceof Memory) {
					fireChanged();
				}
			}
		});
	}

	public void removeListener(StoreListener listener) {
		this.listeners.remove(listener);
	}

	public void addListener(StoreListener listener) {
		this.listeners.add(listener);
	}

	public MemoryStore getMemoryStore() {
		return memoryStore;
	}

	public void setMemoryStore(MemoryStore memoryStore) {
		this.memoryStore = memoryStore;
	}

	private Memory memory() {
		for (Memory memory : organ.getElements(Memory.class)) {
			return memory;
		}
		return null;
	}

	protected void fireIndexChanged() {
		for (StoreListener listener : listeners) {
			listener.indexChanged(getIndex());
		}
	}

	protected void fireChanged() {
		for (StoreListener listener : listeners) {
			listener.changed();
		}
	}

	public int getSize() {
		Memory memory = memory();
		if (memory == null) {
			return 0;
		}
		return memory.getSize();
	}

	public int getIndex() {
		Memory memory = memory();
		if (memory == null) {
			return -1;
		}
		return memory.getIndex();
	}

	public void setIndex(int index) {
		Memory memory = memory();
		if (memory != null) {
			memory.setIndex(index);
		}
	}

	public void read() {
		Memory memory = memory();
		if (memory != null) {
			memoryStore.read(memory, getIndex());
		}
	}

	public void write() {
		Memory memory = memory();
		if (memory != null) {
			memoryStore.write(memory, getIndex());
		}
	}

	public void swap(int index1, int index2) {
	}

	public void clear(int index) {
	}

	public String getTitle(int index) {
		return memoryStore.getTitle(index);
	}

	public void setTitle(int index, String title) {
		memoryStore.setTitle(index, title);
	}
}