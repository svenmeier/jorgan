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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jorgan.disposition.Combination;
import jorgan.disposition.Element;
import jorgan.disposition.Organ;
import jorgan.disposition.Reference;
import jorgan.disposition.event.OrganAdapter;
import jorgan.memory.disposition.Memory;
import jorgan.memory.state.MemoryState;
import jorgan.problem.ElementProblems;
import jorgan.problem.Problem;
import jorgan.problem.Severity;
import bias.Configuration;
import bias.util.MessageBuilder;

/**
 * A manager of {@link MemoryState}s.
 */
public abstract class MemoryManager {

	private static Configuration config = Configuration.getRoot().get(
			MemoryManager.class);

	private List<MemoryManagerListener> listeners = new ArrayList<MemoryManagerListener>();

	private Memory memory;

	private MemoryState memoryState;

	private Organ organ;

	private ElementProblems problems;

	public MemoryManager(Organ organ, ElementProblems problems) {
		this.organ = organ;
		this.problems = problems;

		organ.addOrganListener(new OrganAdapter() {
			@Override
			public void propertyChanged(Element element, String name) {
				if (element == memory) {
					if ("value".equals(name)) {
						write();

						fireIndexChanged();
					} else if ("size".equals(name)) {
						fireChanged();
					} else if ("store".equals(name)) {
						load();
					}
				}
			}

			@Override
			public void referenceAdded(Element element, Reference<?> reference) {
				if (element instanceof Combination) {
					if (memory != null && memory.references(element)) {
						read();
					}
				}
			}

			@Override
			public void referenceChanged(Element element, Reference<?> reference) {
				if (element instanceof Combination) {
					if (memory != null && memory.references(element)) {
						read();
					}
				}
			}

			@Override
			public void elementAdded(Element element) {
				if (memory == null && element instanceof Memory) {
					load();
				}
			}

			@Override
			public void elementRemoved(Element element) {
				if (element == memory) {
					load();
				}
			}
		});

		load();
	}

	public void removeListener(MemoryManagerListener listener) {
		this.listeners.remove(listener);
	}

	public void addListener(MemoryManagerListener listener) {
		this.listeners.add(listener);
	}

	public File getFile() {
		if (memory != null) {
			String store = memory.getStore();
			if (store != null) {
				return resolve(store);
			}
		}
		return null;
	}

	public void setFile(File file) {
		if (memory != null) {
			memory.setStore(file.getPath());
		}
	}

	protected abstract File resolve(String performance);

	protected void fireIndexChanged() {
		for (MemoryManagerListener listener : listeners) {
			listener.indexChanged(getIndex());
		}
	}

	protected void fireChanged() {
		for (MemoryManagerListener listener : listeners) {
			listener.changed();
		}
	}

	public int getSize() {
		if (memoryState == null) {
			return 0;
		} else {
			return memory.getSize();
		}
	}

	public int getIndex() {
		if (memoryState == null) {
			return -1;
		} else {
			return memory.getIndex();
		}
	}

	public void setIndex(int index) {
		if (memory != null) {
			memory.setIndex(index);
		}
	}

	public void read() {
		if (memoryState != null) {
			memoryState.read(memory, getIndex());
		}
	}

	public void write() {
		if (memoryState != null) {
			memoryState.write(memory, getIndex());
		}
	}

	public void swap(int index1, int index2) {
		if (memoryState == null) {
			throw new IllegalStateException();
		} 
	}

	public void clear(int index) {
		if (memoryState == null) {
			throw new IllegalStateException();
		}
		
		memoryState.clear(index);
		
		if (index == memory.getIndex()) {
			write();
		}
	}

	public String getTitle(int index) {
		if (memoryState == null) {
			return "";
		} else {
			return memoryState.getTitle(index);
		}
	}

	public void setTitle(int index, String title) {
		if (memoryState == null) {
			throw new IllegalStateException();
		} 
		
		memoryState.setTitle(index, title);
	}

	public void load() {
		memoryState = null;

		memory = organ.getElement(Memory.class);
		if (memory != null) {
			problems.removeProblem(new Problem(Severity.ERROR, memory, "store",
					null));

			String store = memory.getStore();
			if (store != null) {
				try {
					File file = resolve(store);

					if (file.exists()) {
						// TODO read state from file
						memoryState = new MemoryState();
					} else {
						memoryState = new MemoryState();
					}
				} catch (Exception e) {
					problems.addProblem(new Problem(Severity.ERROR, memory,
							"store", createMessage("load", store)));
				}
			}
		}

		fireChanged();
	}

	public void save() {
		// TODO save to file
	}

	protected String createMessage(String key, Object... args) {
		MessageBuilder builder = new MessageBuilder();

		return config.get(key).read(builder).build(args);
	}

	public boolean isEnabled() {
		return memory != null;
	}

	public boolean isLoaded() {
		return memoryState != null;
	}
}