/**
 * 
 */
package jorgan.disposition;

import java.util.Arrays;

import jorgan.midi.mpl.Command;
import jorgan.midi.mpl.Context;
import jorgan.midi.mpl.NoOp;

public abstract class Message implements Cloneable {

	public static final int STATUS = 0;
	public static final int DATA1 = 0;
	public static final int DATA2 = 0;

	private Command[] commands = new Command[] { new NoOp() };

	public Message change(Command... commands) {
		this.commands = Arrays.copyOf(commands, commands.length);

		return this;
	}

	public Command[] getCommands() {
		return Arrays.copyOf(commands, commands.length);
	}

	public int getLength() {
		return commands.length;
	}

	public Command getCommand(int index) {
		if (index >= commands.length) {
			throw new IllegalArgumentException();
		}
		return commands[index];
	}

	public float process(float value, Context context, int index) {

		return getCommand(index).process(value, context);
	}

	/**
	 * All messages are cloneable.
	 */
	@Override
	public Message clone() {
		try {
			return (Message) super.clone();
		} catch (CloneNotSupportedException ex) {
			throw new Error(ex);
		}
	}
}