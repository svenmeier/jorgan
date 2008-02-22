package jorgan.creative.disposition;

import jorgan.disposition.GenericSound;
import jorgan.util.Null;

public class CreativeSound extends GenericSound {

	private String soundfont;

	private int bank;

	public String getSoundfont() {
		return soundfont;
	}

	public void setSoundfont(String soundfont) {
		if (!Null.safeEquals(this.soundfont, soundfont)) {
			this.soundfont = soundfont;

			fireChanged(true);
		}
	}

	public int getBank() {
		return bank;
	}

	public void setBank(int bank) {
		if (bank != this.bank) {
			this.bank = bank;

			fireChanged(true);
		}
	}
}