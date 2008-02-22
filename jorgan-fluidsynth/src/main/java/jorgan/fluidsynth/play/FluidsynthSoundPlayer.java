package jorgan.fluidsynth.play;

import java.io.IOException;

import javax.sound.midi.ShortMessage;

import jorgan.disposition.event.OrganEvent;
import jorgan.fluidsynth.Fluidsynth;
import jorgan.fluidsynth.disposition.FluidsynthSound;
import jorgan.midi.mpl.Context;
import jorgan.play.SoundPlayer;
import jorgan.session.event.Severity;

/**
 * A player for a {@link FluidsynthSound}.
 */
public class FluidsynthSoundPlayer extends SoundPlayer<FluidsynthSound> {

	private Fluidsynth synth;

	public FluidsynthSoundPlayer(FluidsynthSound sound) {
		super(sound);
	}

	@Override
	protected void setUp() {
		FluidsynthSound sound = getElement();

		removeProblem(Severity.ERROR, "soundfont");
		if (sound.getSoundfont() != null) {
			try {
				synth = new Fluidsynth();
				synth.soundFontLoad(sound.getSoundfont());
			} catch (IOException ex) {
				addProblem(Severity.ERROR, "soundfont", "soundfontLoad", sound
						.getSoundfont());
			}
		}
	}

	@Override
	protected void tearDown() {
		if (synth != null) {
			synth.dispose();
			synth = null;
		}
	}

	@Override
	public void elementChanged(OrganEvent event) {
		super.elementChanged(event);

		FluidsynthSound sound = getElement();
		if (sound.getSoundfont() == null) {
			addProblem(Severity.WARNING, "soundfont", "noSoundfont", sound
					.getSoundfont());
		} else {
			removeProblem(Severity.WARNING, "soundfont");
		}
	}

	@Override
	public void send(ShortMessage message, Context context) {
		if (synth != null) {
			synth.send(message);
		}
	}
}
