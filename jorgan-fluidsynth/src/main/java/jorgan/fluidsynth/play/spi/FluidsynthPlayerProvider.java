package jorgan.fluidsynth.play.spi;

import jorgan.disposition.Element;
import jorgan.fluidsynth.disposition.FluidsynthSound;
import jorgan.fluidsynth.play.FluidsynthSoundPlayer;
import jorgan.play.Player;
import jorgan.play.spi.PlayerProvider;

public class FluidsynthPlayerProvider implements PlayerProvider {

	public Player<FluidsynthSound> createPlayer(Element element) {
		if (element.getClass() == FluidsynthSound.class) {
			return new FluidsynthSoundPlayer((FluidsynthSound) element);
		}
		return null;
	}
}
