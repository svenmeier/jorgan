package jorgan.play.spi;

import jorgan.disposition.Activator;
import jorgan.disposition.Captor;
import jorgan.disposition.Combination;
import jorgan.disposition.Console;
import jorgan.disposition.ContinuousFilter;
import jorgan.disposition.Coupler;
import jorgan.disposition.Element;
import jorgan.disposition.Incrementer;
import jorgan.disposition.Keyboard;
import jorgan.disposition.Keyer;
import jorgan.disposition.Memory;
import jorgan.disposition.MidiInput;
import jorgan.disposition.MidiOutput;
import jorgan.disposition.Rank;
import jorgan.disposition.Regulator;
import jorgan.disposition.Sequence;
import jorgan.disposition.Stop;
import jorgan.disposition.SwitchFilter;
import jorgan.play.ConsolePlayer;
import jorgan.play.ContinuousFilterPlayer;
import jorgan.play.ContinuousPlayer;
import jorgan.play.CouplerPlayer;
import jorgan.play.InitiatorPlayer;
import jorgan.play.MidiInputPlayer;
import jorgan.play.KeyboardPlayer;
import jorgan.play.KeyerPlayer;
import jorgan.play.MidiOutputPlayer;
import jorgan.play.Player;
import jorgan.play.RankPlayer;
import jorgan.play.StopPlayer;
import jorgan.play.SwitchFilterPlayer;
import jorgan.play.SwitchPlayer;

public class DefaultPlayerProvider implements PlayerProvider {

	public Player<? extends Element> createPlayer(Element element) {
		Player<? extends Element> player = null;

		Class clazz = element.getClass();

		if (clazz == MidiInput.class) {
			player = new MidiInputPlayer((MidiInput) element);
		} else if (clazz == Console.class) {
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
		} else if (clazz == SwitchFilter.class) {
			player = new SwitchFilterPlayer((SwitchFilter) element);
		} else if (clazz == Activator.class) {
			player = new SwitchPlayer<Activator>((Activator) element);
		} else if (clazz == Regulator.class) {
			player = new ContinuousPlayer<Regulator>((Regulator) element);
		} else if (clazz == Combination.class) {
			player = new InitiatorPlayer<Combination>((Combination) element);
		} else if (clazz == Incrementer.class) {
			player = new InitiatorPlayer<Incrementer>((Incrementer) element);
		} else if (clazz == Captor.class) {
			player = new SwitchPlayer<Captor>((Captor) element);
		} else if (clazz == Memory.class) {
			player = new ContinuousPlayer<Memory>((Memory) element);
		} else if (clazz == Sequence.class) {
			player = new ContinuousPlayer<Sequence>((Sequence) element);
		} else if (clazz == MidiOutput.class) {
			player = new MidiOutputPlayer<MidiOutput>((MidiOutput) element);
		}

		return player;
	}
}
