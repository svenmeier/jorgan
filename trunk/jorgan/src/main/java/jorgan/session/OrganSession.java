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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jorgan.disposition.Console;
import jorgan.disposition.Element;
import jorgan.disposition.Organ;
import jorgan.disposition.event.OrganAdapter;
import jorgan.disposition.event.OrganListener;
import jorgan.disposition.event.OrganObserver;
import jorgan.play.OrganPlay;
import jorgan.play.Resolver;
import jorgan.play.event.PlayListener;
import jorgan.session.problem.ProblemListener;
import jorgan.session.selection.SelectionEvent;
import jorgan.session.selection.SelectionListener;
import jorgan.session.undo.UndoListener;
import spin.Spin;

/**
 * A session playing or constructing an organ via a GUI. <br>
 * Note that <em>Spin</em> ensures that listener methods are called on the
 * EDT, although a change in disposition or players might be triggered by a
 * change on a MIDI thread.
 * 
 * TODO remove spin dependencies - non-GUI clients don't need Spin
 */
public class OrganSession {

	/**
	 * The file the current organ is associated with.
	 */
	private File file;

	private Organ organ;

	private OrganPlay play;

	private ElementSelection selection;

	private ElementProblems problems;

	private UndoManager undo;

	private List<SessionListener> listeners = new ArrayList<SessionListener>();

	private boolean constructing = false;

	public OrganSession() {
		this(createDefaultOrgan());
	}

	public OrganSession(Organ organ) {
		this(organ, null);
	}

	public OrganSession(Organ organ, File file) {
		if (organ == null) {
			throw new IllegalArgumentException("organ must not be null");
		}
		this.organ = organ;
		this.file = file;

		problems = new ElementProblems();

		play = new OrganPlay(organ, problems, new PlayResolver());

		undo = new UndoManager(organ);

		selection = new ElementSelection();
		selection.addSelectionListener(new SelectionListener() {
			public void selectionChanged(SelectionEvent ev) {
				undo.compound();
			}
		});

		organ.addOrganListener((OrganListener) Spin.over(new OrganAdapter() {
			@Override
			public void elementRemoved(Element element) {
				selection.clear(element);

				problems.removeProblems(element);

				undo.compound();
			}

			@Override
			public void elementAdded(Element element) {
				selection.setSelectedElement(element);

				undo.compound();
			}
		}));

		play.open();
	}

	public void setFile(File file) {
		if (this.file != null && file == null) {
			throw new IllegalArgumentException("file cannot be set to null");
		}

		this.file = file;
	}

	public File getFile() {
		return file;
	}

	public boolean isConstructing() {
		return constructing;
	}

	public void setConstructing(boolean constructing) {
		if (constructing != this.constructing) {
			this.constructing = constructing;

			if (constructing) {
				if (play.isOpen()) {
					play.close();
				}
			} else {
				if (!play.isOpen()) {
					play.open();
				}
			}
			
			for (SessionListener listener : listeners) {
				listener.constructingChanged(constructing);
			}
		}
	}

	public void addListener(SessionListener listener) {
		listeners.add(listener);
	}

	public void removeListener(SessionListener listener) {
		listeners.remove(listener);
	}

	public Organ getOrgan() {
		return organ;
	}

	public OrganPlay getPlay() {
		return play;
	}

	public UndoManager getUndoManager() {
		return undo;
	}

	public ElementSelection getSelection() {
		return selection;
	}

	public ElementProblems getProblems() {
		return problems;
	}

	public void addSelectionListener(SelectionListener listener) {
		selection.addSelectionListener(listener);
	}

	public void removeSelectionListener(SelectionListener listener) {
		selection.removeSelectionListener(listener);
	}

	public void addUndoListener(UndoListener listener) {
		undo.addUndoListener((UndoListener) Spin.over(listener));
	}

	public void removeUndoListener(UndoListener listener) {
		undo.removeUndoListener((UndoListener) Spin.over(listener));
	}

	public void addOrganListener(OrganListener listener) {
		organ.addOrganListener((OrganListener) Spin.over(listener));
	}

	public void removeOrganListener(OrganListener listener) {
		organ.removeOrganListener((OrganListener) Spin.over(listener));
	}

	public void addOrganObserver(OrganObserver observer) {
		organ.addOrganObserver((OrganObserver) Spin.over(observer));
	}

	public void removeOrganObserver(OrganObserver observer) {
		organ.removeOrganObserver((OrganObserver) Spin.over(observer));
	}

	public void addPlayerListener(PlayListener listener) {
		play.addPlayerListener((PlayListener) Spin.over(listener));
	}

	public void removePlayerListener(PlayListener listener) {
		play.removePlayerListener((PlayListener) Spin.over(listener));
	}

	public void addProblemListener(ProblemListener listener) {
		problems.addProblemListener((ProblemListener) Spin.over(listener));
	}

	public void removeProblemListener(ProblemListener listener) {
		problems.removeProblemListener((ProblemListener) Spin.over(listener));
	}

	private class PlayResolver implements Resolver {
		public File resolve(String name) throws IOException {
			return OrganSession.this.resolve(name);
		}
	}

	public File resolve(String name) throws IOException {
		File file = new File(name);

		if (!file.isAbsolute()) {
			if (getFile() == null) {
				file = null;
			} else {
				file = new File(getFile().getParentFile(), name);
			}
		}

		if (file != null) {
			file = file.getCanonicalFile();
		}

		if (file == null || !file.exists()) {
			throw new FileNotFoundException();
		}

		return file;
	}

	private static Organ createDefaultOrgan() {
		Organ organ = new Organ();

		organ.addElement(new Console());

		return organ;
	}

	public void destroy() {
		play.destroy();
		
		for (SessionListener listener : listeners) {
			listener.destroyed();
		}
	}
}