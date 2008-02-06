package jorgan.fluidsynth.play;

import java.io.IOException;

import javax.sound.midi.ShortMessage;

import jorgan.disposition.Output;
import jorgan.disposition.event.OrganEvent;
import jorgan.fluidsynth.Fluidsynth;
import jorgan.fluidsynth.disposition.FluidsynthOutput;
import jorgan.play.OutputPlayer;
import jorgan.session.event.Severity;

/**
 * A player for a {@link Output} element with a {@link FluidsynthOutput}.
 */
public class FluidsynthOutputPlayer extends
		OutputPlayer<FluidsynthOutput> {

	private Fluidsynth synth;

	public FluidsynthOutputPlayer(FluidsynthOutput output) {
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
		FluidsynthOutput output = getElement();

		if (output.getSoundfont() != null) {
			try {
				synth = new Fluidsynth();
				synth.soundFontLoad(output.getSoundfont());
			} catch (IOException ex) {
				addProblem(Severity.ERROR, "name", output.getName(),
						"soundfontUnkown");
			}
		}
	}

	private void unload() {
		if (synth != null) {
			synth.dispose();
			synth = null;
		}
	}

	@Override
	public void send(ShortMessage message) {
		if (synth != null) {
			synth.send(message);
		}
	}
}
