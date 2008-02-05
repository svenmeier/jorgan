package jorgan.creative.play.spi;

import jorgan.creative.disposition.CreativeOutput;
import jorgan.creative.play.CreativeOutputPlayer;
import jorgan.disposition.Element;
import jorgan.play.Player;
import jorgan.play.spi.PlayerProvider;

public class CreativePlayerProvider implements PlayerProvider {

	public Player<CreativeOutput> createPlayer(Element element) {
		if (element instanceof CreativeOutput) {
			return new CreativeOutputPlayer((CreativeOutput) element);
		}
		return null;
	}
}
