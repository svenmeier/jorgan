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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;

import jorgan.disposition.Input.InputMessage;
import jorgan.disposition.InterceptMessage;
import jorgan.disposition.Message;
import jorgan.disposition.Output.OutputMessage;
import jorgan.midi.MessageUtils;
import jorgan.midi.mpl.Context;
import jorgan.play.ConnectorPlayer;
import jorgan.problem.Severity;
import jorgan.sams.disposition.Sams;
import jorgan.sams.disposition.Sams.CancelTabOff;
import jorgan.sams.disposition.Sams.CancelTabOn;
import jorgan.sams.disposition.Sams.TabMessage;
import jorgan.sams.disposition.Sams.TabTurnedOff;
import jorgan.sams.disposition.Sams.TabTurnedOn;
import jorgan.sams.disposition.Sams.TabTurningOff;
import jorgan.sams.disposition.Sams.TabTurningOn;
import jorgan.time.WakeUp;

/**
 * Player for a {@link Sams}.
 */
public class SamsPlayer extends ConnectorPlayer<Sams> {

	private static final int TAB_COUNT = 128;

	private PlayerContext interceptContext = new PlayerContext();

	private Tab[] tabs = new Tab[TAB_COUNT];

	private int energized = 0;

	private List<Tab.Magnet> waitingForEnergize = new LinkedList<Tab.Magnet>();

	public SamsPlayer(Sams sams) {
		super(sams);

		for (int t = 0; t < tabs.length; t++) {
			tabs[t] = new Tab(t);
		}
	}

	@Override
	protected void closeImpl() {
		waitingForEnergize.clear();

		for (Tab tab : tabs) {
			tab.reset();
		}

		super.closeImpl();
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
				if (interceptContext.process(message, datas, false)) {
					if (message instanceof TabTurningOn) {
						Tab tab = getTab(message,
								(int) interceptContext.get(TabTurningOn.TAB));
						if (tab != null) {
							tab.turnOn(datas);
							break;
						}
					} else if (message instanceof TabTurningOff) {
						Tab tab = getTab(message,
								(int) interceptContext.get(TabTurningOff.TAB));
						if (tab != null) {
							tab.turnOff(datas);
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * Overriden to intercept.
	 */
	@Override
	protected void receive(MidiMessage midiMessage) {
		// first update tabs
		onReceived(MessageUtils.getDatas(midiMessage));

		// ... then let elements receive
		super.receive(midiMessage);
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

	protected void energizeOrWait(Tab.Magnet magnet) {
		if (energized < getElement().getMaximum()) {
			energized++;

			magnet.energize();
		} else {
			waitingForEnergize.add(magnet);
		}
	}

	protected void noLongerEnergized(Tab.Magnet magnet) {
		energized--;

		while (energized < getElement().getMaximum()
				&& !waitingForEnergize.isEmpty()) {

			Tab.Magnet waiting = waitingForEnergize.remove(0);
			energized++;
			waiting.energize();
		}
	}

	public class Tab {

		private int index;

		private Boolean state;

		private Magnet onMagnet = new Magnet();

		private Magnet offMagnet = new Magnet();

		private byte[] intercepted;

		public Tab(int index) {
			this.index = index;
		}

		public void reset() {
			offMagnet.cancel();
			onMagnet.cancel();

			state = null;
		}

		private boolean turn(byte[] intercepted, boolean state,
				Magnet toCancel, Magnet toEnergize) {
			this.intercepted = Arrays.copyOf(intercepted, intercepted.length);

			if (this.state == null || this.state != state) {
				toCancel.cancel();
				SamsPlayer.this.energizeOrWait(toEnergize);

				return true;
			}
			return false;
		}

		public boolean turnOn(byte[] intercepted) {
			return turn(intercepted, true, offMagnet, onMagnet);
		}

		public boolean turnOff(byte[] intercepted) {
			return turn(intercepted, false, onMagnet, offMagnet);
		}

		public void onTurnedOn() {
			state = Boolean.TRUE;

			onMagnet.cancel();
		}

		public void onTurnedOff() {
			state = Boolean.FALSE;

			offMagnet.cancel();
		}

		private class Magnet implements WakeUp {
			private boolean energized = false;

			public void energize() {
				if (!this.energized) {
					this.energized = true;

					try {
						SamsPlayer.super.send(intercepted);
					} catch (InvalidMidiDataException invalid) {
						onInvalidMidiData(null, intercepted);
					}

					long duration = getElement().getDuration();
					getOrganPlay().alarm(this, duration);
				}
			}

			public void cancel() {
				if (this.energized) {
					this.energized = false;

					if (onMagnet == this) {
						output(CancelTabOn.class, index);
					} else {
						output(CancelTabOff.class, index);
					}

					noLongerEnergized(this);
				} else {
					waitingForEnergize.remove(this);
				}
			}

			@Override
			public boolean replaces(WakeUp wakeUp) {
				return wakeUp == this;
			}

			@Override
			public void trigger() {
				cancel();
			}
		}
	}
}