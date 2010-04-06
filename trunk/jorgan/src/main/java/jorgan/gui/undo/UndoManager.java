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
package jorgan.gui.undo;

import java.util.ArrayList;
import java.util.List;

import jorgan.disposition.Organ;
import jorgan.disposition.event.Change;
import jorgan.disposition.event.OrganListener;
import jorgan.disposition.event.OrganObserver;
import jorgan.disposition.event.UndoableChange;

/**
 * A manager of {@link UndoableChange}s.
 */
public class UndoManager {

	private List<UndoListener> listeners = new ArrayList<UndoListener>();

	private List<UndoableChange> undos = new ArrayList<UndoableChange>();

	private List<UndoableChange> redos = new ArrayList<UndoableChange>();

	private boolean allowCompound = false;

	private Compound compound;

	private boolean undoing;

	public UndoManager(Organ organ) {
		organ.addOrganObserver(new OrganObserver() {
			public void onChange(Change change) {
				if (change instanceof UndoableChange) {
					add((UndoableChange) change);
				}
			}
		});
	}

	public void addListener(UndoListener listener) {
		listeners.add(listener);
	}

	public void removeListener(UndoListener listener) {
		listeners.remove(listener);
	}

	private void add(UndoableChange change) {
		if (!undoing) {
			if (allowCompound) {
				UndoableChange previous = undos.remove(undos.size() - 1);
				if (previous.replaces(change)) {
					undos.add(previous);
				} else {
					undos.add(new CompoundChange(change, previous));
				}
			} else {
				undos.add(change);
				allowCompound = true;
			}

			redos.clear();

			fireChange();
		}
	}

	private void fireChange() {
		for (UndoListener listener : listeners) {
			listener.done();
		}
	}

	public void compound() {
		if (compound == null) {
			allowCompound = false;
		}
	}

	public void compound(Compound compound) {
		if (this.compound != null) {
			compound.run();
			return;
		}

		allowCompound = false;

		this.compound = compound;
		try {
			compound.run();
		} finally {
			this.compound = null;
		}

		allowCompound = false;
	}

	public boolean canUndo() {
		return !undos.isEmpty();
	}

	public boolean canRedo() {
		return !redos.isEmpty();
	}

	public void undo() {
		allowCompound = false;

		if (!undos.isEmpty()) {
			try {
				undoing = true;

				UndoableChange change = undos.remove(undos.size() - 1);

				redos.add(change);

				change.undo();

				fireChange();
			} finally {
				undoing = false;
			}
		}
	}

	public void redo() {
		allowCompound = false;

		if (!redos.isEmpty()) {
			try {
				undoing = true;
				UndoableChange change = redos.remove(redos.size() - 1);

				undos.add(change);

				change.redo();

				fireChange();
			} finally {
				undoing = false;
			}
		}
	}

	private static class CompoundChange implements UndoableChange {
		private UndoableChange change1;

		private UndoableChange change2;

		public CompoundChange(UndoableChange change1, UndoableChange change2) {
			this.change1 = change1;
			this.change2 = change2;
		}

		public void notify(OrganListener listener) {
		}

		public void undo() {
			change1.undo();
			change2.undo();
		}

		public void redo() {
			change2.redo();
			change1.redo();
		}

		public boolean replaces(UndoableChange change) {
			if (change1.replaces(change)) {
				return true;
			}

			return change2.replaces(change);
		}
	}
}