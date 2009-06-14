package jorgan.play;

import jorgan.disposition.Activator;
import jorgan.disposition.Captor;
import jorgan.disposition.Combination;
import jorgan.disposition.Console;
import jorgan.disposition.Continuous;
import jorgan.disposition.ContinuousFilter;
import jorgan.disposition.Coupler;
import jorgan.disposition.Element;
import jorgan.disposition.GenericSound;
import jorgan.disposition.Incrementer;
import jorgan.disposition.Keyboard;
import jorgan.disposition.Keyer;
import jorgan.disposition.Rank;
import jorgan.disposition.Regulator;
import jorgan.disposition.Stop;
import jorgan.disposition.Switch;
import jorgan.disposition.SwitchFilter;
import jorgan.play.spi.PlayerProvider;

public class DefaultPlayerProvider implements PlayerProvider {

	public Player<? extends Element> createPlayer(Element element) {
		Player<? extends Element> player = null;

		Class<? extends Element> clazz = element.getClass();

		if (clazz == Console.class) {
			player = new ConsolePlayer((Console) element);
		} else if (clazz == Keyboard.class) {
			player = new KeyboardPlayer((Keyboard) element);
		} else if (clazz == Keyer.class) {
			player = new KeyerPlayer((Keyer) element);
		} else if (clazz == Stop.class) {
			player = new StopPlayer((Stop) element);
		} else if (clazz == Coupler.class) {
			player = new CouplerPlayer((Coupler) element);
		} else if (clazz == Rank.class) {
			player = new RankPlayer((Rank) element);
		} else if (clazz == ContinuousFilter.class) {
			player = new ContinuousFilterPlayer((ContinuousFilter) element);
		} else if (clazz == Switch.class) {
			player = new SwitchPlayer<Switch>((Switch) element);
		} else if (clazz == SwitchFilter.class) {
			player = new SwitchFilterPlayer((SwitchFilter) element);
		} else if (clazz == Activator.class) {
			player = new SwitchPlayer<Activator>((Activator) element);
		} else if (clazz == Regulator.class) {
			player = new ContinuousPlayer<Regulator>((Regulator) element);
		} else if (clazz == Combination.class) {
			player = new SwitchPlayer<Combination>((Combination) element);
		} else if (clazz == Incrementer.class) {
			player = new SwitchPlayer<Incrementer>((Incrementer) element);
		} else if (clazz == Captor.class) {
			player = new SwitchPlayer<Captor>((Captor) element);
		} else if (clazz == Continuous.class) {
			player = new ContinuousPlayer<Continuous>((Continuous) element);
		} else if (clazz == GenericSound.class) {
			player = new GenericSoundPlayer<GenericSound>(
					(GenericSound) element);
		}

		return player;
	}
}
