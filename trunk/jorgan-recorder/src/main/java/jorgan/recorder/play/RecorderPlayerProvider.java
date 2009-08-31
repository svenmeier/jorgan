package jorgan.recorder.play;

import jorgan.disposition.Element;
import jorgan.play.Player;
import jorgan.play.SwitchPlayer;
import jorgan.play.spi.PlayerProvider;
import jorgan.recorder.disposition.RecorderSwitch;

public class RecorderPlayerProvider implements PlayerProvider {

	public Player<? extends Element> createPlayer(Element element) {
		Player<? extends Element> player = null;

		if (RecorderSwitch.class.isInstance(element)) {
			player = new SwitchPlayer<RecorderSwitch>((RecorderSwitch) element);
		}

		return player;
	}
}
