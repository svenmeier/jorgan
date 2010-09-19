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

import java.util.ArrayList;
import java.util.List;

import jorgan.midi.mpl.Add;
import jorgan.midi.mpl.Chain;
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
import jorgan.midi.mpl.Tuple;
import jorgan.util.Null;

/**
 * A keyboard.
 */
public class Keyboard extends Element implements Input {

	private String input;

	public Keyboard() {
		// note on, pitch, velocity
		addMessage(new PressKey().change(new Equal(144), new Get(Key.PITCH),
				new Greater(0), new Get(PressKey.VELOCITY)));
		// note on, pitch, -
		addMessage(new ReleaseKey().change(new Equal(144), new Get(Key.PITCH),
				new Equal(0)));
		// note off, pitch, -
		addMessage(new ReleaseKey().change(new Equal(128), new Get(Key.PITCH),
				new NoOp()));
	}

	protected boolean canReference(Class<? extends Element> clazz) {
		return Keyable.class.isAssignableFrom(clazz);
	}

	public void setInput(String input) {
		if (!Null.safeEquals(this.input, input)) {
			String oldInput = this.input;

			this.input = input;

			fireChange(new PropertyChange(oldInput, this.input));
		}
	}

	public String getInput() {
		return input;
	}

	public int getChannel() throws ProcessingException {
		int channel = -1;

		for (Key message : getMessages(Key.class)) {
			Equal equal = Command.get(message.get(Message.STATUS), Equal.class);
			if (equal != null) {
				channel = ((int) equal.getValue()) & 0x0f;
			}
		}

		if (channel == -1) {
			throw new ProcessingException();
		}
		return channel;
	}

	public void setChannel(int channel) {
		for (Key message : getMessages(Key.class)) {
			Tuple tuple = message.getTuple();

			Equal equal = Command.get(tuple.get(Message.STATUS), Equal.class);
			if (equal != null) {
				equal = new Equal((((int) equal.getValue()) & 0xfffffff0)
						+ channel);

				changeMessage(message, tuple.set(Message.STATUS, equal));
			}
		}
	}

	public int getFrom() throws ProcessingException {
		int pitch = 0;
		boolean found = false;

		for (Key message : getMessages(Key.class)) {
			found = true;

			GreaterEqual greaterEqual = Command.get(message.get(Message.DATA1),
					GreaterEqual.class);
			if (greaterEqual != null) {
				pitch = Math.max(pitch, ((int) greaterEqual.getValue()));
			}

			Greater greater = Command.get(message.get(Message.DATA1),
					Greater.class);
			if (greater != null) {
				pitch = Math.max(pitch, ((int) greater.getValue()) + 1);
			}
		}

		if (!found) {
			throw new ProcessingException();
		}
		return pitch;
	}

	public int getTo() throws ProcessingException {
		int pitch = 127;
		boolean found = false;

		for (Key message : getMessages(Key.class)) {
			found = true;

			LessEqual lessEqual = Command.get(message.get(Message.DATA1),
					LessEqual.class);
			if (lessEqual != null) {
				pitch = Math.min(pitch, ((int) lessEqual.getValue()));
			}

			Less less = Command.get(message.get(Message.DATA1), Less.class);
			if (less != null) {
				pitch = Math.min(pitch, ((int) less.getValue()) - 1);
			}
		}

		if (!found) {
			throw new ProcessingException();
		}
		return pitch;
	}

	public int getTranspose() throws ProcessingException {
		int transpose = 0;
		boolean found = false;

		for (Key message : getMessages(Key.class)) {
			found = true;

			Add add = Command.get(message.get(Message.DATA1), Add.class);
			if (add != null) {
				transpose = ((int) add.getValue());
			}

			Sub sub = Command.get(message.get(Message.DATA1), Sub.class);
			if (sub != null) {
				transpose = ((int) sub.getValue()) * -1;
			}
		}

		if (!found) {
			throw new ProcessingException();
		}
		return transpose;
	}

	public void setPitch(int from, int to, int transpose) {

		List<Command> commands = new ArrayList<Command>();

		if (transpose < 0) {
			commands.add(new Sub(-transpose));
		}
		if (transpose > 0) {
			commands.add(new Add(transpose));
		}
		if (to < 127) {
			commands.add(new LessEqual(to));
		}
		if (from > 0) {
			commands.add(new GreaterEqual(from));
		}

		commands.add(new Get(PressKey.PITCH));

		for (Key message : getMessages(Key.class)) {
			if (message.getLength() > Message.DATA1) {
				changeMessage(message, message.getTuple().set(Message.DATA1,
						new Chain(commands)));
			}
		}
	}

	@Override
	public List<Class<? extends Message>> getMessageClasses() {
		List<Class<? extends Message>> classes = super.getMessageClasses();

		classes.add(PressKey.class);
		classes.add(ReleaseKey.class);

		return classes;
	}

	private static class Key extends InputMessage {

		public static final String PITCH = "pitch";

	}

	public static class PressKey extends Key {

		public static final String VELOCITY = "velocity";
	}

	public static class ReleaseKey extends Key {
	}
}