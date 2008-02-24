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

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

import jorgan.disposition.Console;
import jorgan.disposition.Element;
import jorgan.disposition.Message;
import jorgan.disposition.Input.InputMessage;
import jorgan.disposition.Output.OutputMessage;
import jorgan.disposition.event.OrganEvent;
import jorgan.midi.MessageUtils;
import jorgan.midi.mpl.Context;
import jorgan.midi.mpl.ProcessingException;
import jorgan.session.event.Problem;
import jorgan.session.event.Severity;
import bias.Configuration;
import bias.util.MessageBuilder;

/**
 * Abstract base class for all players.
 */
public abstract class Player<E extends Element> {

	private static Configuration config = Configuration.getRoot();

	private OrganPlay organPlay;

	/**
	 * The element played by this player.
	 */
	private final E element;

	/**
	 * Is this player open.
	 */
	private boolean open;

	/**
	 * Create a player for the given element.
	 */
	protected Player(E element) {
		this.element = element;
	}

	public void setOrganPlay(OrganPlay organPlay) {
		if (organPlay == null) {
			tearDown();
			this.organPlay = organPlay;
		} else {
			this.organPlay = organPlay;
			setUp();
		}
	}

	protected void setUp() {

	}

	protected void tearDown() {

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

		for (Message message : getElement().getMessages()) {
			removeProblem(Severity.ERROR, message);
		}

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

	protected void addProblem(Severity severity, Object location, String key,
			Object... args) {

		String message = createMessage(key, args);

		getOrganPlay().addProblem(
				new Problem(severity, element, location, message));
	}

	protected void removeProblem(Severity severity, Object location) {
		getOrganPlay().removeProblem(
				new Problem(severity, element, location, null));
	}

	protected String createMessage(String key, Object[] args) {
		MessageBuilder builder = new MessageBuilder();

		Class<?> clazz = getClass();
		while (true) {
			config.get(clazz).get(key).read(builder);
			if (builder.hasPattern()) {
				break;
			}
			if (clazz == Player.class) {
				break;
			} else {
				clazz = clazz.getSuperclass();
			}
		}

		return builder.build(args);
	}

	public void elementChanged(OrganEvent event) {
		// only 'real' changes (identifiable by non-null event) trigger {@link
		// #tearDown()} and {@link #setUp()}.
		if (event != null) {
			tearDown();
			setUp();
		}
	}

	protected final void received(ShortMessage shortMessage, Context context) {
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
			int iStatus = Math.round(status);

			float data1 = message.processData1(0.0f, context);
			if (Float.isNaN(status)) {
				// abort processing
				return;
			}
			int iData1 = Math.round(data1);

			float data2 = message.processData2(0.0f, context);
			if (Float.isNaN(status)) {
				// abort processing
				return;
			}
			int iData2 = Math.round(data2);

			ShortMessage shortMessage;
			try {
				shortMessage = MessageUtils.createShortMessage(iStatus, iData1,
						iData2);
			} catch (InvalidMidiDataException ex) {
				addProblem(Severity.ERROR, message, "messageInvalid", iStatus,
						iData1, iData2);
				return;
			}

			send(shortMessage, context);

			if (organPlay != null) {
				organPlay.fireOutputProduced();
			}
		} catch (ProcessingException ex) {
			addProblem(Severity.ERROR, message, "messageIllegal", ex
					.getPattern());
		}
	}

	/**
	 * Send a message - default implementation forwards messages to referring
	 * {@link Console}s.
	 */
	protected void send(ShortMessage message, Context context) {
		for (Console console : organPlay.getOrgan().getReferrer(element,
				Console.class)) {
			Player<? extends Element> player = getOrganPlay()
					.getPlayer(console);
			if (player != null) {
				player.send(message, context);
			}
		}
	}

	public E getElement() {
		return element;
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
			addProblem(Severity.ERROR, message, "messageIllegal", ex
					.getPattern());
			return false;
		}
		return true;
	}
}