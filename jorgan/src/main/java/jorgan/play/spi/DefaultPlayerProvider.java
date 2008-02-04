package jorgan.play.spi;

import jorgan.disposition.Activator;
import jorgan.disposition.BasicOutput;
import jorgan.disposition.Captor;
import jorgan.disposition.Console;
import jorgan.disposition.ContinuousFilter;
import jorgan.disposition.Coupler;
import jorgan.disposition.Element;
import jorgan.disposition.Initiator;
import jorgan.disposition.Input;
import jorgan.disposition.Keyboard;
import jorgan.disposition.Keyer;
import jorgan.disposition.Memory;
import jorgan.disposition.Rank;
import jorgan.disposition.Regulator;
import jorgan.disposition.Sequence;
import jorgan.disposition.Stop;
import jorgan.disposition.SwitchFilter;
import jorgan.play.BasicOutputPlayer;
import jorgan.play.ConsolePlayer;
import jorgan.play.ContinuousFilterPlayer;
import jorgan.play.ContinuousPlayer;
import jorgan.play.CouplerPlayer;
import jorgan.play.InitiatorPlayer;
import jorgan.play.InputPlayer;
import jorgan.play.KeyboardPlayer;
import jorgan.play.KeyerPlayer;
import jorgan.play.Player;
import jorgan.play.RankPlayer;
import jorgan.play.StopPlayer;
import jorgan.play.SwitchFilterPlayer;
import jorgan.play.SwitchPlayer;

public class DefaultPlayerProvider implements PlayerProvider {

	public Player<? extends Element> createPlayer(Element element) {
		Player<? extends Element> player = null;

		if (element instanceof Input) {
			player = new InputPlayer((Input) element);
		} else if (element instanceof Console) {
			player = new ConsolePlayer((Console) element);
		} else if (element instanceof Keyboard) {
			player = new KeyboardPlayer((Keyboard) element);
		} else if (element instanceof Keyer) {
			player = new KeyerPlayer((Keyer) element);
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
		} else if (element instanceof Activator) {
			player = new SwitchPlayer<Activator>((Activator) element);
		} else if (element instanceof Regulator) {
			player = new ContinuousPlayer<Regulator>((Regulator) element);
		} else if (element instanceof Initiator) {
			player = new InitiatorPlayer<Initiator>((Initiator) element);
		} else if (element instanceof Captor) {
			player = new SwitchPlayer<Captor>((Captor) element);
		} else if (element instanceof Memory) {
			player = new ContinuousPlayer<Memory>((Memory) element);
		} else if (element instanceof Sequence) {
			player = new ContinuousPlayer<Sequence>((Sequence) element);
		} else if (element instanceof BasicOutput) {
			player = new BasicOutputPlayer<BasicOutput>((BasicOutput) element);
		}

		return player;
	}
}
