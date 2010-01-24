package jorgan.sysex.disposition;

import jorgan.disposition.GenericSound;
import jorgan.util.Null;

public class SysexSound extends GenericSound {

	private String open;

	private String close;

	public void setOpen(String open) {
		if (!Null.safeEquals(this.open, open)) {
			String oldOpen = this.open;

			this.open = open;

			fireChange(new PropertyChange(oldOpen, this.open));
		}
	}

	public String getOpen() {
		return open;
	}

	public void setClose(String close) {
		if (!Null.safeEquals(this.close, close)) {
			String oldClose = this.close;

			this.close = close;

			fireChange(new PropertyChange(oldClose, this.close));
		}
	}

	public String getClose() {
		return close;
	}
}
