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
		int channel = 0;

		for (HandleKey message : getMessages(HandleKey.class)) {
			Equal equal = Command.create(message.getStatus()).get(Equal.class);
			if (equal != null) {
				int status = ((int) equal.getValue());
				channel = status & 0x0f;
			}
		}

		return channel;
	}

	public void setChannel(int channel) throws ProcessingException {
		for (HandleKey message : getMessages(HandleKey.class)) {
			Command command = Command.create(message.getStatus());

			Equal equal = command.get(Equal.class);
			if (equal != null) {
				command = new Equal((((int) equal.getValue()) & 0xfffffff0)
						+ channel);

				changeMessage(message, command.toString(), message.getData1(),
						message.getData2());
			}
		}
	}

	public int getFrom() throws ProcessingException {
		int pitch = 0;

		for (HandleKey message : getMessages(HandleKey.class)) {
			GreaterEqual greaterEqual = Command.create(message.getData1()).get(
					GreaterEqual.class);
			if (greaterEqual != null) {
				pitch = ((int) greaterEqual.getValue());
			}
		}

		return pitch;
	}

	public int getTo() throws ProcessingException {
		int pitch = 127;

		for (HandleKey message : getMessages(HandleKey.class)) {
			LessEqual lessEqual = Command.create(message.getData1()).get(
					LessEqual.class);
			if (lessEqual != null) {
				pitch = ((int) lessEqual.getValue());
			}
		}

		return pitch;
	}

	public int getTranspose() throws ProcessingException {
		int transpose = 0;

		for (HandleKey message : getMessages(HandleKey.class)) {
			Add add = Command.create(message.getData1()).get(Add.class);
			if (add != null) {
				transpose = ((int) add.getValue());
			}
		}

		return transpose;
	}

	public void setData1(int from, int to, int transpose) throws ProcessingException {

		for (HandleKey message : getMessages(HandleKey.class)) {
			Command command = new GreaterEqual(from, new LessEqual(to, new Add(null, transpose)));

			changeMessage(message, message.getStatus(), command.toString(),
					message.getData2());
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