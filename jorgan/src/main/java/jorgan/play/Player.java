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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

import jorgan.disposition.Console;
import jorgan.disposition.Element;
import jorgan.disposition.Message;
import jorgan.disposition.Input.InputMessage;
import jorgan.disposition.Output.OutputMessage;
import jorgan.disposition.event.OrganEvent;
import jorgan.midi.mpl.Context;
import jorgan.midi.mpl.ProcessingException;
import jorgan.session.event.Error;
import jorgan.session.event.Warning;
import bias.Configuration;
import bias.util.MessageBuilder;

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
	private final E element;

	/**
	 * Is this player open.
	 */
	private boolean open;

	private boolean warnDevice;

	private boolean warnMessages;

	/**
	 * Create a player for the given element.
	 */
	protected Player(E element) {
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

		removeError("message");

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

	protected void addWarning(String property, Object value, String key) {

		String message = config.get("Warning." + key).read(
				new MessageBuilder()).build(value);
		getOrganPlay().getProblems().addProblem(
				new Warning(element, property, message));
	}

	protected void removeWarning(String property) {
		getOrganPlay().getProblems().removeProblem(
				new Warning(element, property, null));
	}

	protected void addError(String property, Object value, String key) {
		String message = config.get("Error." + key).read(
				new MessageBuilder()).build(value);
		getOrganPlay().getProblems().addProblem(
				new Error(element, property, message));
	}

	protected void removeError(String property) {
		getOrganPlay().getProblems().removeProblem(
				new Error(element, property, null));
	}

	public void elementChanged(OrganEvent event) {
		if (!element.hasMessages() && warnMessages) {
			addWarning("messages", null, "messagesMissing");
		} else {
			removeWarning("messages");
		}
	}

	public final void input(ShortMessage shortMessage, Context context) {
		for (InputMessage message : element.getMessages(InputMessage.class)) {
			if (process(shortMessage.getStatus(), shortMessage.getData1(),
					shortMessage.getData2(), message, context)) {
				input(message, context);

				organPlay.fireInputAccepted();
			}
		}
	}

	/**
	 * Read input from the given message - default implementation does nothing.
	 * 
	 * @param message
	 *            message
	 * @param context
	 *            the message context
	 */
	protected void input(InputMessage message, Context context) {

	}

	protected final void output(OutputMessage message, Context context) {
		try {
			float status = message.processStatus(0.0f, context);
			if (Float.isNaN(status)) {
				// abort processing
				return;
			}
			float data1 = message.processData1(0.0f, context);
			if (Float.isNaN(status)) {
				// abort processing
				return;
			}
			float data2 = message.processData2(0.0f, context);
			if (Float.isNaN(status)) {
				// abort processing
				return;
			}

			ShortMessage shortMessage;
			try {
				shortMessage = createShortMessage(status, data1, data2);
			} catch (InvalidMidiDataException ex) {
				addError("messages", Math.round(status) + ","
						+ Math.round(data1) + "," + Math.round(data2), "messageInvalid");
				return;
			}

			output(shortMessage, context);

			if (organPlay != null) {
				organPlay.fireOutputProduced();
			}
		} catch (ProcessingException ex) {
			addError("messages", ex.getPattern(), "illegalMessage");
		}
	}

	private ShortMessage createShortMessage(float status, float data1,
			float data2) throws InvalidMidiDataException {

		ShortMessage shortMessage = new ShortMessage();
		
		// status isn't checked in ShortMessage#setMessage(int, int, int)
		int iStatus = Math.round(status);
		if (iStatus < 0 || iStatus > 255) {
			throw new InvalidMidiDataException("status out of range: " + iStatus);
		}
		int iData1 = Math.round(data1);
		int iData2 = Math.round(data2);

		shortMessage.setMessage(iStatus, iData1, iData2);
		
		return shortMessage;
	}

	/**
	 * Output a message - default implementation forwards message to referring
	 * {@link Console}s.
	 */
	public void output(ShortMessage message, Context context) {
		Set<Console> consoles = organPlay.getOrgan().getReferrer(element,
				Console.class);
		for (Console console : consoles) {
			Player player = getOrganPlay().getPlayer(console);
			player.output(message, context);
		}
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

	public boolean getWarnMessages() {
		return warnMessages;
	}

	public void setWarnMessages(boolean warnMessages) {
		this.warnMessages = warnMessages;
	}

	public void received(ShortMessage message) {
	}

	protected class PlayerContext implements Context {

		private Map<String, Float> map = new HashMap<String, Float>();

		public float get(String name) {
			Float temp = map.get(name);
			if (temp == null) {
				return Float.NaN;
			} else {
				return temp;
			}
		}

		public void set(String name, float value) {
			map.put(name, value);
		}

		public void clear() {
			map.clear();
		}
	};

	protected boolean process(int status, int data1, int data2,
			Message message, Context context) {
		try {
			if (Float.isNaN(message.processStatus(status, context))) {
				return false;
			}
			if (Float.isNaN(message.processData1(data1, context))) {
				return false;
			}
			if (Float.isNaN(message.processData2(data2, context))) {
				return false;
			}
		} catch (ProcessingException ex) {
			addError("messages", ex.getPattern(), "messageIllegal");
			return false;
		}
		return true;
	}
}