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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jorgan.disposition.Combination;
import jorgan.disposition.Element;
import jorgan.disposition.Organ;
import jorgan.disposition.Reference;
import jorgan.disposition.event.OrganAdapter;
import jorgan.memory.disposition.Memory;
import jorgan.memory.io.MemoryStateStream;
import jorgan.memory.state.MemoryState;
import jorgan.problem.ElementProblems;
import jorgan.problem.Problem;
import jorgan.problem.Severity;
import jorgan.util.Null;
import bias.Configuration;
import bias.util.MessageBuilder;

/**
 * A manager of {@link MemoryState}s.
 */
public abstract class Storage {

	private static Configuration config = Configuration.getRoot().get(
			Storage.class);

	private List<StorageListener> listeners = new ArrayList<StorageListener>();

	private Memory memory;

	private MemoryState state;

	private Organ organ;

	private ElementProblems problems;

	private boolean modified = false;

	public Storage(Organ organ, ElementProblems problems) {
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
					} else if ("storage".equals(name)) {
						load();
					}
				}
			}

			@Override
			public void indexedPropertyAdded(Element element, String name,
					Object value) {
				if (Element.REFERENCE.equals(name)) {
					Reference<?> reference = (Reference<?>) value;
					if (element instanceof Combination) {
						if (memory != null && memory.references(element)) {
							readReference((Combination) element, reference);
						}
					} else if (element == memory) {
						read();
					}
				}
			}

			@Override
			public void indexedPropertyChanged(Element element, String name,
					Object value) {
				if (Element.REFERENCE.equals(name)) {
					Reference<?> reference = (Reference<?>) value;
					if (element instanceof Combination) {
						if (memory != null && memory.references(element)) {
							readReference((Combination) element, reference);
						}
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

	public void removeListener(StorageListener listener) {
		if (!this.listeners.remove(listener)) {
			throw new IllegalArgumentException("unknown listener");
		}
	}

	public void addListener(StorageListener listener) {
		this.listeners.add(listener);
	}

	public File getFile() {
		if (memory != null) {
			String storage = memory.getStorage();
			if (storage != null) {
				return resolve(storage);
			}
		}
		return null;
	}

	/**
	 * Set the file to be used as storage. Creates an empty storage if the file
	 * doesn't exist.
	 */
	public void setFile(File file) {
		if (memory != null) {
			if (file == null) {
				memory.setStorage(null);
			} else {
				if (!file.exists()) {
					try {
						new MemoryStateStream().write(new MemoryState(), file);
					} catch (IOException ex) {
						// load will show problem
					}
				}

				memory.setStorage(deresolve(file));
			}
		}
	}

	protected abstract File resolve(String performance);

	protected abstract String deresolve(File file);

	protected void fireIndexChanged() {
		for (StorageListener listener : listeners) {
			listener.indexChanged(getIndex());
		}
	}

	protected void fireChanged() {
		for (StorageListener listener : listeners) {
			listener.changed();
		}
	}

	public int getSize() {
		if (state == null) {
			return 0;
		} else {
			return memory.getSize();
		}
	}

	public int getIndex() {
		if (state == null) {
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

	public MemoryState getState() {
		return state;
	}

	public void read() {
		if (state != null) {
			state.read(memory, getIndex());

			modified = true;
		}
	}

	public void readReference(Combination combination, Reference<?> reference) {
		if (state != null) {
			state.read(combination, reference, getIndex());

			modified = true;
		}
	}

	public void write() {
		if (state != null) {
			state.write(memory, getIndex());
		}
	}

	public void swap(int index1, int index2) {
		if (state == null) {
			throw new IllegalStateException();
		}

		state.swap(index1, index2);

		markModified();

		if (index1 == memory.getIndex() || index2 == memory.getIndex()) {
			write();
		}

		fireChanged();
	}

	public void clear(int index) {
		if (state == null) {
			throw new IllegalStateException();
		}

		state.clear(index);

		markModified();

		if (index == memory.getIndex()) {
			write();
		}

		fireChanged();
	}

	public String getTitle(int index) {
		if (state == null) {
			return "";
		} else {
			return state.getTitle(index);
		}
	}

	public void setTitle(int index, String title) {
		if (state == null) {
			throw new IllegalStateException();
		}

		String oldTitle = state.getTitle(index);
		if (!Null.safeEquals(oldTitle, title)) {
			state.setTitle(index, title);

			markModified();

			fireChanged();
		}
	}

	public void load() {
		state = null;

		memory = organ.getElement(Memory.class);
		if (memory != null) {
			problems.removeProblem(new Problem(Severity.ERROR, memory,
					"storage", null));

			String storage = memory.getStorage();
			if (storage != null) {
				try {
					File file = resolve(storage);

					state = new MemoryStateStream().read(file);

					write();
				} catch (Exception e) {
					problems.addProblem(new Problem(Severity.ERROR, memory,
							"storage", createMessage("load", storage)));
				}
			}
		}

		modified = false;

		fireChanged();
	}

	public void save() throws IOException {
		String storage = memory.getStorage();

		new MemoryStateStream().write(state, resolve(storage));

		modified = false;
	}

	protected String createMessage(String key, Object... args) {
		MessageBuilder builder = new MessageBuilder();

		return config.get(key).read(builder).build(args);
	}

	public boolean isEnabled() {
		return memory != null;
	}

	public boolean isLoaded() {
		return state != null;
	}

	public boolean isModified() {
		return modified;
	}

	protected void markModified() {
		this.modified = true;
	}
}