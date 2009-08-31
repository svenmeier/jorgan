package jorgan.memory.play;

import jorgan.disposition.Element;
import jorgan.memory.disposition.Memory;
import jorgan.play.ContinuousPlayer;
import jorgan.play.Player;
import jorgan.play.spi.PlayerProvider;

public class MemoryPlayerProvider implements PlayerProvider {

	public Player<? extends Element> createPlayer(Element element) {
		Player<? extends Element> player = null;

		Class<? extends Element> clazz = element.getClass();

		if (clazz == Memory.class) {
			player = new ContinuousPlayer<Memory>((Memory) element);
		}

		return player;
	}
}
