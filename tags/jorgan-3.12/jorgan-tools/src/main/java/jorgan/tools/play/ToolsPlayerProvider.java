package jorgan.tools.play;

import jorgan.disposition.Element;
import jorgan.play.Player;
import jorgan.play.spi.PlayerProvider;
import jorgan.tools.disposition.PanicSwitch;

public class ToolsPlayerProvider implements PlayerProvider {

	public Player<? extends Element> createPlayer(Element element) {
		Player<? extends Element> player = null;

		if (element instanceof PanicSwitch) {
			player = new PanicSwitchPlayer((PanicSwitch) element);
		}

		return player;
	}
}
