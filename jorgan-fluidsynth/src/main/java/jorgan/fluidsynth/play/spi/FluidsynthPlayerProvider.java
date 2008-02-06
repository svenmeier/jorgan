package jorgan.fluidsynth.play.spi;

import jorgan.disposition.Element;
import jorgan.fluidsynth.disposition.FluidsynthOutput;
import jorgan.fluidsynth.play.FluidsynthOutputPlayer;
import jorgan.play.Player;
import jorgan.play.spi.PlayerProvider;

public class FluidsynthPlayerProvider implements PlayerProvider {

	public Player<FluidsynthOutput> createPlayer(Element element) {
		if (element.getClass() == FluidsynthOutput.class) {
			return new FluidsynthOutputPlayer((FluidsynthOutput) element);
		}
		return null;
	}
}
