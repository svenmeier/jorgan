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
package jorgan.session;

import java.util.ArrayList;
import java.util.List;

import jorgan.disposition.Organ;
import jorgan.disposition.event.Change;
import jorgan.disposition.event.OrganListener;
import jorgan.disposition.event.OrganObserver;
import jorgan.disposition.event.UndoableChange;
import jorgan.session.event.UndoListener;

/**
 * A manager of {@link UndoableChange}s.
 */
public class UndoManager {

	private List<UndoListener> listeners = new ArrayList<UndoListener>();

	private List<UndoableChange> undos = new ArrayList<UndoableChange>();

	private List<UndoableChange> redos = new ArrayList<UndoableChange>();
	
	private boolean compound = false;

	private boolean inProgress;

	public UndoManager(Organ organ) {
		organ.addOrganObserver(new OrganObserver() {
			public void onChange(Change change) {
				if (change instanceof UndoableChange) {
					add((UndoableChange) change);
				}
			}
		});
	}

	public void addUndoListener(UndoListener listener) {
		listeners.add(listener);
	}

	public void removeUndoListener(UndoListener listener) {
		listeners.remove(listener);
	}

	private void add(UndoableChange change) {
		if (!inProgress) {
			if (compound) {
				undos.add(new CompoundChange(change, undos.remove(undos.size() - 1)));
			} else {
				undos.add(change);
				compound = true;
			}
			
			redos.clear();

			fireChange();
		}
	}

	private void fireChange() {
		for (UndoListener listener : listeners) {
			listener.changed();
		}
	}

	public void compound() {
		compound = false;
	}
	
	public boolean canUndo() {
		return !undos.isEmpty();
	}

	public boolean canRedo() {
		return !redos.isEmpty();
	}

	public void undo() {
		compound = false;
		
		if (!undos.isEmpty()) {
			try {
				inProgress = true;

				UndoableChange change = undos.remove(undos.size() - 1);

				redos.add(change);

				change.undo();
				
				fireChange();
			} finally {
				inProgress = false;
			}
		}
	}

	public void redo() {
		compound = false;

		if (!redos.isEmpty()) {
			try {
				inProgress = true;
				UndoableChange change = redos.remove(redos.size() - 1);

				undos.add(change);

				change.redo();
				
				fireChange();
			} finally {
				inProgress = false;
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
	}
}