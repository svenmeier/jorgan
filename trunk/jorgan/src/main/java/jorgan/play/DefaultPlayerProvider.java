package jorgan.play;

import jorgan.disposition.Console;
import jorgan.disposition.Continuous;
import jorgan.disposition.ContinuousFilter;
import jorgan.disposition.Coupler;
import jorgan.disposition.Element;
import jorgan.disposition.GenericSound;
import jorgan.disposition.Keyboard;
import jorgan.disposition.Keyer;
import jorgan.disposition.Panic;
import jorgan.disposition.Rank;
import jorgan.disposition.Stop;
import jorgan.disposition.Switch;
import jorgan.disposition.SwitchFilter;
import jorgan.play.spi.PlayerProvider;

public class DefaultPlayerProvider implements PlayerProvider {

	public Player<? extends Element> createPlayer(Element element) {
		Player<? extends Element> player = null;

		// specific players first
		if (element instanceof Console) {
			player = new ConsolePlayer<Console>((Console) element);
		} else if (element instanceof Keyboard) {
			player = new KeyboardPlayer((Keyboard) element);
		} else if (element instanceof Keyer) {
			player = new KeyerPlayer((Keyer) element);
		} else if (element instanceof Panic) {
			player = new PanicPlayer((Panic) element);
		} else if (element instanceof Stop) {
			player = new StopPlayer((Stop) element);
		} else if (element instanceof Coupler) {
			player = new CouplerPlayer((Coupler) element);
		} else if (element instanceof Rank) {
			player = new RankPlayer((Rank) element);
		} else if (element instanceof ContinuousFilter) {
			player = new ContinuousFilterPlayer((ContinuousFilter) element);
		} else if (element instanceof SwitchFilter) {
			player = new SwitchFilterPlayer((SwitchFilter) element);
		} else if (element instanceof GenericSound) {
			player = new GenericSoundPlayer<GenericSound>(
					(GenericSound) element);
		} else if (element instanceof Switch) {
			player = new SwitchPlayer<Switch>((Switch) element);
		} else if (element instanceof Continuous) {
			player = new ContinuousPlayer<Continuous>((Continuous) element);
		}

		return player;
	}
}
