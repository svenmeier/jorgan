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

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;

import jorgan.disposition.Connector;
import jorgan.disposition.Element;
import jorgan.disposition.Input.InputMessage;
import jorgan.disposition.Message;
import jorgan.disposition.Output.OutputMessage;
import jorgan.midi.mpl.Context;
import jorgan.midi.mpl.ContextImpl;
import jorgan.problem.Problem;
import jorgan.problem.Severity;
import bias.Configuration;
import bias.util.MessageBuilder;

/**
 * Abstract base class for all players.
 */
public abstract class Player<E extends Element> {

	private static Configuration config = Configuration.getRoot();

	private OrganPlay organPlay;

	private PlayerContext inputContext = new PlayerContext();

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
			destroy();
			this.organPlay = organPlay;
		} else {
			this.organPlay = organPlay;
		}
	}

	protected File resolve(String name) throws IOException {
		return organPlay.resolve(name);
	}

	protected void destroy() {

	}

	public OrganPlay getOrganPlay() {
		return organPlay;
	}

	public final E getElement() {
		return element;
	}

	protected Player<?> getPlayer(Element element) {
		return getOrganPlay().getPlayer(element);
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

	public void update() {
	}

	public void onReceived(byte[] datas) {
		for (InputMessage message : element.getMessages(InputMessage.class)) {
			try {
				if (inputContext.process(message, datas, false)) {
					onInput(message, inputContext);
				}
			} catch (InvalidMidiDataException e) {
				onInvalidMidiData(message, datas);
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
	protected void onInput(InputMessage message, Context context) {

	}

	private byte[] defaultDatas = new byte[3];

	protected final void output(OutputMessage message, PlayerContext context) {
		byte[] datas;
		if (message.getLength() == 3) {
			// small optimization for short messages
			datas = defaultDatas;
			datas[0] = 0;
			datas[1] = 0;
			datas[2] = 0;
		} else {
			datas = new byte[message.getLength()];
		}

		try {
			if (context.process(message, datas, true)) {
				onOutput(datas, context);
			}
		} catch (InvalidMidiDataException e) {
			onInvalidMidiData(message, datas);
		}
	}

	private void onInvalidMidiData(Message message, byte[] datas) {
		StringBuilder builder = new StringBuilder();
		for (byte data : datas) {
			if (builder.length() > 0) {
				builder.append(", ");
			}
			builder.append(data & 0xff);
		}

		addProblem(Severity.ERROR, message, "messageInvalid",
				builder.toString());
	}

	/**
	 * Handle message output - default implementation lets referring
	 * {@link Connector}s send the message.
	 * 
	 * @throws InvalidMidiDataException
	 * 
	 * @see {@link ConnectorPlayer#send(byte[])}
	 */
	protected void onOutput(byte[] datas, Context context)
			throws InvalidMidiDataException {

		for (Connector connector : organPlay.getOrgan().getReferrer(element,
				Connector.class)) {
			ConnectorPlayer<?> player = (ConnectorPlayer<?>) getPlayer(connector);
			if (player != null) {
				player.send(datas);
			}
		}
	}

	protected void fireSent(MidiMessage message) {
		if (getOrganPlay() != null) {
			getOrganPlay().fireSent(this.getElement(), message);
		}
	}

	protected void fireReceived(MidiMessage midiMessage) {
		if (getOrganPlay() != null) {
			getOrganPlay().fireReceived(this.getElement(), midiMessage);
		}
	}

	public class PlayerContext extends ContextImpl {

		public boolean process(Message message, byte[] datas, boolean write)
				throws InvalidMidiDataException {
			if (message.getLength() != datas.length) {
				return false;
			}

			boolean valid = true;
			for (int d = 0; d < datas.length; d++) {
				float processed = message.process(datas[d] & 0xff, this, d);
				if (Float.isNaN(processed)) {
					return false;
				}
				if (write) {
					int toWrite = Math.round(processed);
					if (toWrite < 0 || toWrite > 255) {
						valid = false;
					}
					datas[d] = (byte) toWrite;
				}
			}

			if (!valid) {
				throw new InvalidMidiDataException();
			}
			return true;
		}
	}
}