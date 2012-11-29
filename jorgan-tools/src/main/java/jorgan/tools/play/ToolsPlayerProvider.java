package jorgan.tools.play;

import jorgan.disposition.Element;
import jorgan.play.Player;
import jorgan.play.spi.PlayerProvider;
import jorgan.tools.disposition.ConnectionSwitch;
import jorgan.tools.disposition.PanicSwitch;
import jorgan.tools.disposition.ReengageSwitch;

public class ToolsPlayerProvider implements PlayerProvider {

	public Player<? extends Element> createPlayer(Element element) {
		Player<? extends Element> player = null;

		if (element instanceof ConnectionSwitch) {
			player = new ConnectionSwitchPlayer((ConnectionSwitch) element);
		} else if (element instanceof PanicSwitch) {
			player = new PanicSwitchPlayer((PanicSwitch) element);
		} else if (element instanceof ReengageSwitch) {
			player = new ReengageSwitchPlayer((ReengageSwitch) element);
		}

		return player;
	}
}
