package jorgan.creative.play;

import java.io.IOException;

import jorgan.creative.SoundFontManager;
import jorgan.creative.disposition.CreativeOutput;
import jorgan.disposition.Output;
import jorgan.disposition.event.OrganEvent;
import jorgan.play.MidiOutputPlayer;
import jorgan.session.event.Severity;

/**
 * A player for a {@link Output} element with a {@link CreativeOutput}.
 */
public class CreativeOutputPlayer extends MidiOutputPlayer<CreativeOutput> {

	private boolean loaded;

	public CreativeOutputPlayer(CreativeOutput output) {
		super(output);
	}

	@Override
	protected void destroy() {
		unload();
	}

	@Override
	public void elementChanged(OrganEvent event) {
		unload();
		load();
	}

	private void load() {
		CreativeOutput output = getElement();

		loaded = false;
		removeProblem(Severity.ERROR, "name");

		if (output.getSoundfont() != null && output.getDevice() != null) {
			try {
				SoundFontManager manager = new SoundFontManager();
				
				int max = manager.getNumDevices();
				for (int d = 0; d < max; d++) {
					if (manager.getDeviceName(d).equals(output.getDevice())) {
						new SoundFontManager().loadBank(d, output.getBank(), output
								.getSoundfont());
						break;
					}
				}

				loaded = true;
			} catch (IOException ex) {
				addProblem(Severity.ERROR, "soundfont", output.getSoundfont(),
						"soundFontLoad");
			}
		}
	}

	private void unload() {
		CreativeOutput output = getElement();

		if (loaded) {
			try {
				new SoundFontManager().clearBank(0, output.getBank());
			} catch (IOException ex) {
			}
		}
	}
}
