package jorgan.sysex.disposition;

import jorgan.disposition.GenericSound;
import jorgan.util.Null;

/**
 * A {@link GenericSound} subclass offering sending Sysex messages on opening
 * and/or closing.
 * 
 * @see #getOpen()
 * @see #getClose()
 **/
public class SysexSound extends GenericSound {

	private String open;

	private String close;

	public void setOpen(String open) {
		// ignore equal values
		if (!Null.safeEquals(this.open, open)) {
			String oldOpen = this.open;

			this.open = open;

			// notify listeners of a change
			fireChange(new PropertyChange(oldOpen, this.open));
		}
	}

	public String getOpen() {
		return open;
	}

	public void setClose(String close) {
		// ignore equal values
		if (!Null.safeEquals(this.close, close)) {
			String oldClose = this.close;

			this.close = close;

			// notify listeners of a change
			fireChange(new PropertyChange(oldClose, this.close));
		}
	}

	public String getClose() {
		return close;
	}
}
