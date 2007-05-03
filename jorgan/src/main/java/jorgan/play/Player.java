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
package jorgan.play;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.ShortMessage;

import jorgan.disposition.Element;
import jorgan.disposition.event.OrganEvent;
import bias.Configuration;

/**
 * Abstract base class for all players.
 */
public abstract class Player {

	private static Configuration config = Configuration.getRoot().get(
			Player.class);

	private OrganPlay organPlay;

	/**
	 * The element played by this player.
	 */
	private Element element;

	/**
	 * Is this player open.
	 */
	private boolean open;

	private boolean warnDevice;

	private boolean warnMessage;

	/**
	 * The problems.
	 */
	private List<Problem> problems = new ArrayList<Problem>();

	private int errorCount = 0;

	private int warningCount = 0;

	/**
	 * Create a player for the given element.
	 */
	public Player(Element element) {
		config.read(this);

		this.element = element;
	}

	public void setOrganPlay(OrganPlay organPlay) {
		this.organPlay = organPlay;
	}

	public OrganPlay getOrganPlay() {
		return organPlay;
	}

	/**
	 * Test is this player is open.
	 * 
	 * @return <code>true</code> if this player is open
	 */
	public boolean isOpen() {
		return open;
	}

	/**
	 * Open this player.
	 */
	public final void open() {
		if (open) {
			throw new IllegalStateException("already open");
		}
		open = true;

		openImpl();
	}

	/**
	 * Perform subclass specific initialization on opening of this player, e.g.
	 * aquire MIDI resources. <br>
	 * This default implementation does nothing.
	 */
	protected void openImpl() {
	}

	/**
	 * Close this player.
	 */
	public final void close() {
		if (!open) {
			throw new IllegalStateException("already closed");
		}
		open = false;

		closeImpl();
	}

	/**
	 * Perform subclass specific cleanup on closing of this player, e.g. release
	 * MIDI resources. <br>
	 * This default implementation does nothing.
	 */
	protected void closeImpl() {
	}

	protected void input(ShortMessage message) {

	}

	protected void addProblem(Problem problem) {
		if (problem == null) {
			throw new IllegalArgumentException("problem must not be null");
		}
		if (!problems.contains(problem)) {
			problems.add(problem);
			if (Problem.WARNING.equals(problem.getLevel())) {
				warningCount++;
			}
			if (Problem.ERROR.equals(problem.getLevel())) {
				errorCount++;
			}
			fireProblemAdded(problem);
		}
	}

	protected void removeProblem(Problem problem) {
		if (problem == null) {
			throw new IllegalArgumentException("problem must not be null");
		}
		if (problems.contains(problem)) {
			problems.remove(problem);
			if (Problem.WARNING.equals(problem.getLevel())) {
				warningCount--;
			}
			if (Problem.ERROR.equals(problem.getLevel())) {
				errorCount--;
			}
			fireProblemsRemoved(problem);
		}
	}

	private void fireProblemAdded(Problem problem) {
		if (organPlay != null) {
			organPlay.fireProblemAdded(this, problem);
		}
	}

	private void fireProblemsRemoved(Problem problem) {
		if (organPlay != null) {
			organPlay.fireProblemRemoved(this, problem);
		}
	}

	protected void fireInputAccepted() {
		if (organPlay != null) {
			organPlay.fireInputAccepted();
		}
	}

	protected void fireOutputProduced() {
		if (organPlay != null) {
			organPlay.fireOutputProduced();
		}
	}

	public boolean hasWarnings() {
		return warningCount > 0;
	}

	public boolean hasErrors() {
		return errorCount > 0;
	}

	public List<Problem> getProblems() {
		return new ArrayList<Problem>(problems);
	}

	public void elementChanged(OrganEvent event) {
	}

	public void messageReceived(ShortMessage message) {
	}

	public Element getElement() {
		return element;
	}

	public boolean getWarnDevice() {
		return warnDevice;
	}

	public void setWarnDevice(boolean warnDevice) {
		this.warnDevice = warnDevice;
	}

	public boolean getWarnMessage() {
		return warnMessage;
	}

	public void setWarnMessage(boolean warnMessage) {
		this.warnMessage = warnMessage;
	}
}