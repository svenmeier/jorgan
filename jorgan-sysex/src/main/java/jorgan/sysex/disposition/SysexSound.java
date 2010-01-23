package jorgan.sysex.disposition;

import jorgan.disposition.GenericSound;

public class SysexSound extends GenericSound {

	private String open;

	private String close;

	public void setOpen(String open) {
		this.open = open;
	}

	public String getOpen() {
		return open;
	}

	public void setClose(String close) {
		this.close = close;
	}

	public String getClose() {
		return close;
	}
}
