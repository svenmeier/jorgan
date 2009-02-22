/**
 * 
 */
package jorgan.disposition;

import jorgan.midi.mpl.Command;
import jorgan.midi.mpl.Context;
import jorgan.midi.mpl.ProcessingException;

public abstract class Message implements Cloneable {

	private String status = "";

	private String data1 = "";

	private String data2 = "";

	private transient Command statusCommand;

	private transient Command data1Command;

	private transient Command data2Command;

	protected Message change(String status, String data1, String data2) {
		this.status = status;
		this.data1 = data1;
		this.data2 = data2;

		statusCommand = null;
		data1Command = null;
		data2Command = null;

		return this;
	}

	protected Message change(Command status, Command data1, Command data2) {
		this.status = status.toString();
		this.data1 = data1.toString();
		this.data2 = data2.toString();

		statusCommand = status;
		data1Command = data1;
		data2Command = data2;

		return this;
	}

	public String getData1() {
		return data1;
	}

	public String getData2() {
		return data2;
	}

	public String getStatus() {
		return status;
	}

	public float processStatus(float status, Context context)
			throws ProcessingException {
		if (statusCommand == null) {
			statusCommand = Command.create(this.status);
		}
		return statusCommand.process(status, context);
	}

	public float processData1(float data1, Context context)
			throws ProcessingException {
		if (data1Command == null) {
			data1Command = Command.create(this.data1);
		}
		return data1Command.process(data1, context);
	}

	public float processData2(float data2, Context context)
			throws ProcessingException {
		if (data2Command == null) {
			data2Command = Command.create(this.data2);
		}
		return data2Command.process(data2, context);
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