package jorgan.executor.play;

import jorgan.disposition.Element;
import jorgan.executor.disposition.Executor;
import jorgan.play.Player;
import jorgan.play.SwitchPlayer;
import jorgan.play.spi.PlayerProvider;

public class ExecutorPlayerProvider implements PlayerProvider {

	public Player<? extends Element> createPlayer(Element element) {
		Player<? extends Element> player = null;

		if (Executor.class.isInstance(element)) {
			player = new SwitchPlayer<Executor>((Executor) element);
		}

		return player;
	}
}
