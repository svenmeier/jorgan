package jorgan.fluidsynth.disposition;

import jorgan.disposition.Sound;
import jorgan.util.Null;

public class FluidsynthSound extends Sound {

	private String soundfont;
	
	private int channels = 32;

	public String getSoundfont() {
		return soundfont;
	}

	public void setSoundfont(String soundfont) {
		if (!Null.safeEquals(this.soundfont, soundfont)) {
			this.soundfont = soundfont;

			fireChanged(true);
		}
	}
	
	public int getChannels() {
		return channels;
	}
	
	public void setChannels(int channels) {
		if (channels < 0) {
			throw new IllegalArgumentException("channels must be greater or equal 0");
		}
		if (this.channels != channels) {
			this.channels = channels;
			
			fireChanged(true);
		}
	}
}