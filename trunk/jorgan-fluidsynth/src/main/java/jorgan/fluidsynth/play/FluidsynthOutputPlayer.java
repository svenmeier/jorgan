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
	protected void setUp() {
		FluidsynthOutput output = getElement();

		if (output.getSoundfont() == null) {
			addProblem(Severity.WARNING, "soundfont", output.getSoundfont(),
			"noSoundfont");
		} else {
			try {
				synth = new Fluidsynth();
				synth.soundFontLoad(output.getSoundfont());
			} catch (IOException ex) {
				addProblem(Severity.ERROR, "soundfont", output.getSoundfont(),
						"soundfontLoad");
			}
		}
	}

	@Override
	protected void tearDown() {
		if (synth != null) {
			synth.dispose();
			synth = null;
		}
		
		removeProblem(Severity.ERROR, "soundfont");
		removeProblem(Severity.WARNING, "soundfont");
	}
	
	@Override
	public void elementChanged(OrganEvent event) {
		// only 'real' changes (identifiable by non-null event)
		if (event != null) {
			tearDown();
			setUp();
		}
	}

	@Override
	public void send(ShortMessage message) {
		if (synth != null) {
			synth.send(message);
		}
	}
}
