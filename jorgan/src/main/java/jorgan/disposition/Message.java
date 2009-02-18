/**
 * 
 */
package jorgan.disposition;

import jorgan.midi.mpl.Context;
import jorgan.midi.mpl.Node;
import jorgan.midi.mpl.ProcessingException;

public abstract class Message implements Cloneable {

	private String status = "";

	private String data1 = "";

	private String data2 = "";

	private transient Node statusNode;

	private transient Node data1Node;

	private transient Node data2Node;

	protected Message change(String status, String data1, String data2) {
		this.status = status;
		this.data1 = data1;
		this.data2 = data2;

		statusNode = null;
		data1Node = null;
		data2Node = null;

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
		if (statusNode == null) {
			statusNode = Node.create(this.status);
		}
		return statusNode.process(status, context);
	}

	public float processData1(float data1, Context context)
			throws ProcessingException {
		if (data1Node == null) {
			data1Node = Node.create(this.data1);
		}
		return data1Node.process(data1, context);
	}

	public float processData2(float data2, Context context)
			throws ProcessingException {
		if (data2Node == null) {
			data2Node = Node.create(this.data2);
		}
		return data2Node.process(data2, context);
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