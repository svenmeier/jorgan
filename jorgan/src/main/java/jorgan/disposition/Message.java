/**
 * 
 */
package jorgan.disposition;

import jorgan.midi.mpl.Context;
import jorgan.midi.mpl.ProcessingException;
import jorgan.midi.mpl.Processor;

public abstract class Message implements Cloneable {

	private String status = "";

	private String data1 = "";

	private String data2 = "";

	private transient Processor statusProcessor;

	private transient Processor data1Processor;

	private transient Processor data2Processor;

	protected Message change(String status, String data1, String data2) {
		this.status = status;
		this.data1 = data1;
		this.data2 = data2;

		statusProcessor = null;
		data1Processor = null;
		data2Processor = null;
		
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
		if (statusProcessor == null) {
			statusProcessor = new Processor(this.status);
		}
		return statusProcessor.process(status, context);
	}

	public float processData1(float data1, Context context)
			throws ProcessingException {
		if (data1Processor == null) {
			data1Processor = new Processor(this.data1);
		}
		return data1Processor.process(data1, context);
	}

	public float processData2(float data2, Context context)
			throws ProcessingException {
		if (data2Processor == null) {
			data2Processor = new Processor(this.data2);
		}
		return data2Processor.process(data2, context);
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