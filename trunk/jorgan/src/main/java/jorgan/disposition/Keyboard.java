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
package jorgan.disposition;

import java.util.Set;

import jorgan.midi.mpl.Add;
import jorgan.midi.mpl.Command;
import jorgan.midi.mpl.Equal;
import jorgan.midi.mpl.GreaterEqual;
import jorgan.midi.mpl.LessEqual;
import jorgan.midi.mpl.ProcessingException;
import jorgan.util.Null;

/**
 * A keyboard.
 */
public class Keyboard extends Element implements Input {

	private String input;

	public Keyboard() {
		// note on, pitch, velocity
		addMessage(new PressKey().change("equal 144", "get pitch",
				"greater 0 | get velocity"));
		// note on, pitch, -
		addMessage(new ReleaseKey().change("equal 144", "get pitch", "equal 0"));
		// note off, pitch, -
		addMessage(new ReleaseKey().change("equal 128", "get pitch", ""));
	}

	protected boolean canReference(Class<? extends Element> clazz) {
		return Keyable.class.isAssignableFrom(clazz);
	}

	public void setInput(String input) {
		if (!Null.safeEquals(this.input, input)) {
			String oldInput = this.input;

			this.input = input;

			fireChange(new UndoablePropertyChange(oldInput, this.input));
		}
	}

	public String getInput() {
		return input;
	}

	public int getChannel() throws ProcessingException {
		int channel = Integer.MAX_VALUE;

		for (HandleKey message : getMessages(HandleKey.class)) {
			Equal equal = Command.create(message.getStatus()).get(Equal.class);
			if (equal != null) {
				int status = ((int) equal.getValue());
				channel = status & 0x0f;
			}
		}

		if (channel == Integer.MAX_VALUE) {
			throw new ProcessingException("TODO");
		}
		return channel;
	}

	public void setChannel(int channel) throws ProcessingException {
		getChannel();

		for (HandleKey message : getMessages(HandleKey.class)) {
			Command command = Command.create(message.getStatus());
			Equal equal = command.get(Equal.class);
			if (equal != null) {
				equal.setValue((((int) equal.getValue()) & 0xfffffff0)
						+ channel);

				changeMessage(message, command.toString(), message.getData1(),
						message.getData2());
			}
		}
	}

	public int getFrom() throws ProcessingException {
		int pitch = Integer.MAX_VALUE;

		for (HandleKey message : getMessages(HandleKey.class)) {
			GreaterEqual greaterEqual = Command.create(message.getData1()).get(
					GreaterEqual.class);
			if (greaterEqual != null) {
				pitch = ((int) greaterEqual.getValue());
			}

		}

		if (pitch == Integer.MAX_VALUE) {
			throw new ProcessingException("TODO");
		}
		return pitch;
	}

	public void setFrom(int from) throws ProcessingException {
		getFrom();

		for (HandleKey message : getMessages(HandleKey.class)) {
			Command command = Command.create(message.getData1());
			GreaterEqual greaterEqual = command.get(GreaterEqual.class);
			if (greaterEqual != null) {
				greaterEqual.setValue(from);

				changeMessage(message, message.getStatus(), command.toString(),
						message.getData2());
			}
		}
	}

	public int getTo() throws ProcessingException {
		int pitch = Integer.MAX_VALUE;

		for (HandleKey message : getMessages(HandleKey.class)) {
			LessEqual lessEqual = Command.create(message.getData1()).get(
					LessEqual.class);
			if (lessEqual != null) {
				pitch = ((int) lessEqual.getValue());
			}
		}

		if (pitch == Integer.MAX_VALUE) {
			throw new ProcessingException("TODO");
		}
		return pitch;
	}

	public void setTo(int to) throws ProcessingException {
		getTo();

		for (HandleKey message : getMessages(HandleKey.class)) {
			Command command = Command.create(message.getData1());
			LessEqual lessEqual = command.get(LessEqual.class);
			if (lessEqual != null) {
				lessEqual.setValue(to);

				changeMessage(message, message.getStatus(), command.toString(),
						message.getData2());
			}
		}
	}

	public int getTranspose() throws ProcessingException {
		int transpose = Integer.MAX_VALUE;

		for (HandleKey message : getMessages(HandleKey.class)) {
			Add add = Command.create(message.getData1()).get(
					Add.class);
			if (add != null && add.getName() == null) {
				transpose = ((int) add.getValue());
			}
		}

		if (transpose == Integer.MAX_VALUE) {
			throw new ProcessingException("TODO");
		}
		return transpose;
	}

	public void setTranspose(int transpose) throws ProcessingException {
		getTo();

		for (HandleKey message : getMessages(HandleKey.class)) {
			Command command = Command.create(message.getData1());
			Add add = command.get(Add.class);
			if (add != null) {
				add.setValue(transpose);

				changeMessage(message, message.getStatus(), command.toString(),
						message.getData2());
			}
		}
	}

	@Override
	public Set<Class<? extends Message>> getMessageClasses() {
		Set<Class<? extends Message>> names = super.getMessageClasses();

		names.add(PressKey.class);
		names.add(ReleaseKey.class);

		return names;
	}

	private static class HandleKey extends InputMessage {

	}

	public static class PressKey extends HandleKey {

		public static final String PITCH = "pitch";

		public static final String VELOCITY = "velocity";
	}

	public static class ReleaseKey extends HandleKey {

		public static final String PITCH = "pitch";
	}
}