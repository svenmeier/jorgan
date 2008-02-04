package jorgan.play.spi;

import jorgan.disposition.Element;
import jorgan.play.Player;

public interface PlayerProvider {

	public Player<? extends Element> createPlayer(Element element);
}
