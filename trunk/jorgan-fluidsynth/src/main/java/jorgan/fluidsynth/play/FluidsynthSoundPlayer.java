package jorgan.fluidsynth.play;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import jorgan.disposition.event.OrganEvent;
import jorgan.fluidsynth.Fluidsynth;
import jorgan.fluidsynth.disposition.FluidsynthSound;
import jorgan.play.SoundPlayer;
import jorgan.session.event.Severity;

/**
 * A player for a {@link FluidsynthSound}.
 */
public class FluidsynthSoundPlayer extends SoundPlayer<FluidsynthSound> {

	private Logger logger = Logger.getLogger(FluidsynthSoundPlayer.class
			.getName());

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
			} catch (Error err) {
				logger.log(Level.WARNING, "unable to use Fluidsynth", err);
				addProblem(Severity.ERROR, null, "native");
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
	protected boolean send(int channel, int command, int data1, int data2) {
		if (synth == null) {
			return false;
		}
		
		synth.send(channel, command, data1, data2);
		
		return true;
	}
}
