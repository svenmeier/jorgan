/*
 * jOrgan - Java Virtual Organ
 * Copyright (C) 2003 Sven Meier
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package jorgan.sams.play;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;

import jorgan.disposition.InterceptMessage;
import jorgan.disposition.Message;
import jorgan.disposition.Input.InputMessage;
import jorgan.disposition.Output.OutputMessage;
import jorgan.midi.mpl.Context;
import jorgan.play.ConsolePlayer;
import jorgan.problem.Severity;
import jorgan.sams.disposition.SamsConsole;
import jorgan.sams.disposition.SamsConsole.TabMessage;
import jorgan.sams.disposition.SamsConsole.TabTurningOff;
import jorgan.sams.disposition.SamsConsole.CancelTabOff;
import jorgan.sams.disposition.SamsConsole.TabTurningOn;
import jorgan.sams.disposition.SamsConsole.CancelTabOn;
import jorgan.sams.disposition.SamsConsole.TabTurnedOff;
import jorgan.sams.disposition.SamsConsole.TabTurnedOn;

/**
 * Player for a {@link SamsConsole}.
 */
public class SamsConsolePlayer extends ConsolePlayer<SamsConsole> {

	private static final int TAB_COUNT = 128;

	private PlayerContext interceptContext = new PlayerContext();

	private Tab[] tabs = new Tab[TAB_COUNT];

	public SamsConsolePlayer(SamsConsole console) {
		super(console);

		for (int t = 0; t < tabs.length; t++) {
			tabs[t] = new Tab(t);
		}
	}

	@Override
	protected void closeImpl() {
		for (Tab tab : tabs) {
			tab.reset();
		}

		super.closeImpl();
	}

	@Override
	public void onAlarm(long now) {
		for (Tab tab : tabs) {
			tab.checkDuration(now);
		}
	}

	private Tab getTab(Message message, int index) {
		if (index < 0 || index >= tabs.length) {
			addProblem(Severity.ERROR, message, "tabInvalid", index);
			return null;
		}
		return tabs[index];
	}

	/**
	 * Overriden to intercept.
	 * 
	 * @throws InvalidMidiDataException
	 */
	@Override
	public void send(byte[] datas) throws InvalidMidiDataException {
		for (OutputMessage message : getElement().getMessages(
				OutputMessage.class)) {
			if (message instanceof InterceptMessage) {
				if (interceptContext.process(message, datas)) {
					if (message instanceof TabTurningOn) {
						Tab tab = getTab(message, (int) interceptContext
								.get(TabTurningOn.TAB));
						if (tab != null) {
							tab.turnOn();
						}
					} else if (message instanceof TabTurningOff) {
						Tab tab = getTab(message, (int) interceptContext
								.get(TabTurningOff.TAB));
						if (tab != null) {
							tab.turnOff();
						}
					}
				}
			}
		}

		super.send(datas);
	}

	/**
	 * Overriden to let encoding decode tab changes.
	 */
	@Override
	protected void receive(MidiMessage midiMessage) {
		super.receive(midiMessage);

		onReceived(midiMessage);
	}

	@Override
	protected void onInput(InputMessage message, Context context) {
		if (message instanceof TabTurnedOn) {
			Tab tab = getTab(message, (int) context.get(TabMessage.TAB));
			if (tab != null) {
				tab.onTurnedOn();
			}
		} else if (message instanceof TabTurnedOff) {
			Tab tab = getTab(message, (int) context.get(TabMessage.TAB));
			if (tab != null) {
				tab.onTurnedOff();
			}
		}
	}

	private void output(Class<? extends OutputMessage> type, int tab) {
		for (OutputMessage message : getElement().getMessages(type)) {
			interceptContext.set(TabMessage.TAB, tab);

			output(message, interceptContext);
		}
	}

	protected void onOutput(byte[] datas, Context context)
			throws InvalidMidiDataException {
		// let super implementation send
		super.send(datas);
	}

	public class Tab {

		private int index;

		private Magnet onMagnet = new Magnet();

		private Magnet offMagnet = new Magnet();

		public Tab(int index) {
			this.index = index;
		}

		public void reset() {
			offMagnet.off();
			onMagnet.off();
		}

		public void checkDuration(long time) {
			onMagnet.checkDuration(time);
			offMagnet.checkDuration(time);
		}

		public void turnOn() {
			offMagnet.off();
			onMagnet.on();
		}

		public void turnOff() {
			onMagnet.off();
			offMagnet.on();
		}

		public void onTurnedOn() {
			onMagnet.off();
		}

		public void onTurnedOff() {
			offMagnet.off();
		}

		private class Magnet {
			private long offTime = Long.MAX_VALUE;

			private boolean isOn() {
				return offTime < Long.MAX_VALUE;
			}

			public void on() {
				if (!isOn()) {
					offTime = System.currentTimeMillis()
							+ getElement().getDuration();

					getOrganPlay().getClock().alarm(getElement(), offTime);
				}
			}

			public void off() {
				if (isOn()) {
					offTime = Long.MAX_VALUE;

					if (onMagnet == this) {
						output(CancelTabOn.class, index);
					} else {
						output(CancelTabOff.class, index);
					}
				}
			}

			public void checkDuration(long time) {
				if (isOn()) {
					if (offTime <= time) {
						off();
					}
				}
			}
		}

	}
}