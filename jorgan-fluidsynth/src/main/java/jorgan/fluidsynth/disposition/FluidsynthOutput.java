package jorgan.fluidsynth.disposition;

import jorgan.disposition.Output;
import jorgan.util.Null;

public class FluidsynthOutput extends Output {

	private String soundfont;

	public String getSoundfont() {
		return soundfont;
	}

	public void setSoundfont(String soundfont) {
		if (!Null.safeEquals(this.soundfont, soundfont)) {
			this.soundfont = soundfont;
			
			fireChanged(true);
		}		
	}
}
