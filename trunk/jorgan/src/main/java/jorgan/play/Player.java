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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

import jorgan.disposition.Element;
import jorgan.disposition.Message.InputMessage;
import jorgan.disposition.Message.OutputMessage;
import jorgan.disposition.event.OrganEvent;
import jorgan.util.math.ProcessingException;
import bias.Configuration;

/**
 * Abstract base class for all players.
 */
public abstract class Player<E extends Element> {

	private static Configuration config = Configuration.getRoot().get(
			Player.class);

	private OrganPlay organPlay;

	/**
	 * The element played by this player.
	 */
	private E element;

	/**
	 * Is this player open.
	 */
	private boolean open;

	private boolean warnDevice;

	/**
	 * The problems.
	 */
	private List<Problem> problems = new ArrayList<Problem>();

	private int errorCount = 0;

	private int warningCount = 0;

	private boolean warnMessages;

	private Map<String, Float> values = new HashMap<String, Float>();

	/**
	 * Create a player for the given element.
	 */
	public Player(E element) {
		config.read(this);

		this.element = element;
	}

	public void setOrganPlay(OrganPlay organPlay) {
		this.organPlay = organPlay;
	}

	public OrganPlay getOrganPlay() {
		return organPlay;
	}

	protected Map<String, Float> getValues() {
		values.clear();
		return values;
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

	protected void addProblem(Problem problem) {
		if (problem == null) {
			throw new IllegalArgumentException("problem must not be null");
		}
		if (!problems.contains(problem)) {
			problems.add(problem);
			if (problem instanceof Warning) {
				warningCount++;
			}
			if (problem instanceof Error) {
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
			if (problem instanceof Warning) {
				warningCount--;
			}
			if (problem instanceof Error) {
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
		removeProblem(new Error("messages"));

		if (!element.hasMessages() && warnMessages) {
			addProblem(new Warning("messages"));
		} else {
			removeProblem(new Warning("messages"));
		}
	}

	public final void input(ShortMessage shortMessage) {
		Element element = getElement();

		try {
			for (InputMessage message : element.getMessages(InputMessage.class)) {
				Map<String, Float> values = getValues();

				if (Float.isNaN(message.processStatus(shortMessage.getStatus(), values))) {
					continue;
				}
				if (Float.isNaN(message.processData1(shortMessage.getData1(), values))) {
					continue;
				}
				if (Float.isNaN(message.processData2(shortMessage.getData2(), values))) {
					continue;
				}

				input(message, values);

				organPlay.fireInputAccepted();
			}
		} catch (ProcessingException ex) {
			addProblem(new Error("messages", ex.getPattern()));
		}
	}

	/**
	 * Read input from the given message - default implementation does nothing.
	 * 
	 * @param message
	 *            message
	 */
	protected void input(InputMessage message, Map<String, Float> values) {

	}

	protected final void output(OutputMessage message, Map<String, Float> values) {
		try {
			float status = message.processStatus(Float.NaN, values);
			float data1 = message.processData1(Float.NaN, values);
			float data2 = message.processData2(Float.NaN, values);

			ShortMessage shortMessage;
			try {
				shortMessage = new ShortMessage();
				shortMessage.setMessage(Math.round(status), Math.round(data1),
						Math.round(data2));

				output(shortMessage);
			} catch (InvalidMidiDataException ex) {
				addProblem(new Error("messages"));
			}

			if (organPlay != null) {
				organPlay.fireOutputProduced();
			}
		} catch (ProcessingException ex) {
			addProblem(new Error("messages", ex.getPattern()));
		}
	}

	/**
	 * Write output - default implementation does nothing.
	 */
	protected void output(ShortMessage message) {
	}

	public E getElement() {
		return element;
	}

	public boolean getWarnDevice() {
		return warnDevice;
	}

	public void setWarnDevice(boolean warnDevice) {
		this.warnDevice = warnDevice;
	}

	public boolean getWarnRules() {
		return warnMessages;
	}

	public void setWarnRules(boolean warnRules) {
		this.warnMessages = warnRules;
	}

	public void received(ShortMessage message) {
	}
}