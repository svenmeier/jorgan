/**
 * 
 */
package jorgan.disposition;

import jorgan.midi.mpl.Context;
import jorgan.midi.mpl.ProcessingException;
import jorgan.midi.mpl.Processor;

public abstract class Message implements Comparable<Message> {

	private String status = "";

	private String data1 = "";

	private String data2 = "";

	private transient Processor statusProcessor;

	private transient Processor data1Processor;

	private transient Processor data2Processor;

	public Message init(String status, String data1, String data2) {
		setStatus(status);
		setData1(data1);
		setData2(data2);

		return this;
	}

	public String getData1() {
		return data1;
	}

	public void setData1(String data1) {
		this.data1 = data1;

		data1Processor = null;
	}

	public String getData2() {
		return data2;
	}

	public void setData2(String data2) {
		this.data2 = data2;

		data2Processor = null;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;

		statusProcessor = null;
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

	public int compareTo(Message m) {
		return getOrder() - m.getOrder();
	}

	protected abstract int getOrder();
}