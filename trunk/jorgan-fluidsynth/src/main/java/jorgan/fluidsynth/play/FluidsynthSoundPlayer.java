package jorgan.fluidsynth.play;

import java.io.IOException;

import jorgan.disposition.event.OrganEvent;
import jorgan.fluidsynth.Fluidsynth;
import jorgan.fluidsynth.disposition.FluidsynthSound;
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
	protected int getChannelCount() {
		FluidsynthSound sound = getElement();

		return sound.getChannels();
	}
	
	@Override
	protected void setUp() {
		FluidsynthSound sound = getElement();

		removeProblem(Severity.ERROR, "soundfont");
		if (sound.getSoundfont() != null) {
			try {
				synth = new Fluidsynth(sound.getChannels());
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
	protected void send(int channel, int command, int data1, int data2) {
		if (synth != null) {
			synth.send(channel, command, data1, data2);
		}
	}
}
