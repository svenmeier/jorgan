/**
 * 
 */
package jorgan.disposition;

import jorgan.midi.mpl.Command;
import jorgan.midi.mpl.Context;
import jorgan.midi.mpl.NoOp;
import jorgan.midi.mpl.Tuple;

public abstract class Message implements Cloneable {

	public static final int STATUS = 0;
	public static final int DATA1 = 1;
	public static final int DATA2 = 2;

	private Tuple tuple = new Tuple(new NoOp());

	public Message change(Tuple tuple) {
		this.tuple = tuple;

		return this;
	}

	public Message change(Command... commands) {
		this.tuple = new Tuple(commands);

		return this;
	}

	public Command get(int index) {
		return tuple.get(index);
	}

	public Tuple getTuple() {
		return tuple;
	}

	public int getLength() {
		return tuple.getLength();
	}

	public float process(float value, Context context, int index) {

		return tuple.get(index).process(value, context);
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