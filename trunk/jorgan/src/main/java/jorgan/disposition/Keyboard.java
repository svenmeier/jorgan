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
import jorgan.midi.mpl.Get;
import jorgan.midi.mpl.Greater;
import jorgan.midi.mpl.GreaterEqual;
import jorgan.midi.mpl.Less;
import jorgan.midi.mpl.LessEqual;
import jorgan.midi.mpl.NoOp;
import jorgan.midi.mpl.ProcessingException;
import jorgan.midi.mpl.Sub;
import jorgan.util.Null;

/**
 * A keyboard.
 */
public class Keyboard extends Element implements Input {

	private String input;

	public Keyboard() {
		// note on, pitch, velocity
		addMessage(new PressKey().change(new Equal(144).toString(), new Get(
				"pitch").toString(), new Greater(0, new Get("velocity"))
				.toString()));
		// note on, pitch, -
		addMessage(new ReleaseKey().change(new Equal(144).toString(), new Get(
				"pitch").toString(), new Equal(0).toString()));
		// note off, pitch, -
		addMessage(new ReleaseKey().change(new Equal(128).toString(), new Get(
				"pitch").toString(), new NoOp().toString()));
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
		boolean found = false;

		for (HandleKey message : getMessages(HandleKey.class)) {
			found = true;

			Equal equal = Command.create(message.getStatus()).get(Equal.class);
			if (equal != null) {
				int status = ((int) equal.getValue());
				channel = status & 0x0f;
			}
		}

		if (!found) {
			throw new ProcessingException("");
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
		boolean found = false;

		for (HandleKey message : getMessages(HandleKey.class)) {
			found = true;

			GreaterEqual greaterEqual = Command.create(message.getData1()).get(
					GreaterEqual.class);
			if (greaterEqual != null) {
				pitch = Math.max(pitch, ((int) greaterEqual.getValue()));
			}

			Greater greater = Command.create(message.getData1()).get(
					Greater.class);
			if (greater != null) {
				pitch = Math.max(pitch, ((int) greater.getValue()) + 1);
			}
		}

		if (!found) {
			throw new ProcessingException("");
		}
		return pitch;
	}

	public int getTo() throws ProcessingException {
		int pitch = 127;
		boolean found = false;

		for (HandleKey message : getMessages(HandleKey.class)) {
			found = true;

			LessEqual lessEqual = Command.create(message.getData1()).get(
					LessEqual.class);
			if (lessEqual != null) {
				pitch = Math.min(pitch, ((int) lessEqual.getValue()));
			}

			Less less = Command.create(message.getData1()).get(Less.class);
			if (less != null) {
				pitch = Math.min(pitch, ((int) less.getValue()) - 1);
			}
		}

		if (!found) {
			throw new ProcessingException("");
		}
		return pitch;
	}

	public int getTranspose() throws ProcessingException {
		int transpose = 0;
		boolean found = false;

		for (HandleKey message : getMessages(HandleKey.class)) {
			found = true;

			Add add = Command.create(message.getData1()).get(Add.class);
			if (add != null) {
				transpose = ((int) add.getValue());
			}

			Sub sub = Command.create(message.getData1()).get(Sub.class);
			if (sub != null) {
				transpose = ((int) sub.getValue()) * -1;
			}
		}

		if (!found) {
			throw new ProcessingException("");
		}
		return transpose;
	}

	public void setPitch(int from, int to, int transpose)
			throws ProcessingException {

		Command command = new Get(PressKey.PITCH);
		if (transpose != 0) {
			command = new Add(null, transpose, command);
		}
		if (to < 127) {
			command = new LessEqual(to, command);
		}
		if (from > 0) {
			command = new GreaterEqual(from, command);
		}

		for (HandleKey message : getMessages(HandleKey.class)) {
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