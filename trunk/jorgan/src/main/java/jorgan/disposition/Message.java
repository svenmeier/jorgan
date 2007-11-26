/**
 * 
 */
package jorgan.disposition;

public abstract class Message {

	private String status = "";

	private String data1 = "";

	private String data2 = "";

	public Message init(String status, String data1, String data2) {
		this.status = status;
		this.data1 = data1;
		this.data2 = data2;
		return this;
	}
	
	public String getData1() {
		return data1;
	}

	public void setData1(String data1) {
		this.data1 = data1;
	}

	public String getData2() {
		return data2;
	}

	public void setData2(String data2) {
		this.data2 = data2;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public static abstract class InputMessage extends Message {
	}
	
	public static abstract class OutputMessage extends Message {
	}
}