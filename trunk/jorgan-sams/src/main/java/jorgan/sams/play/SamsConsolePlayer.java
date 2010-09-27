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
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

import jorgan.disposition.Input.InputMessage;
import jorgan.midi.MessageUtils;
import jorgan.midi.mpl.Context;
import jorgan.play.ConsolePlayer;
import jorgan.play.PlayerContext;
import jorgan.sams.disposition.SamsConsole;
import jorgan.sams.disposition.SamsConsole.MagnetMessage;
import jorgan.sams.disposition.SamsConsole.OffMagnetOff;
import jorgan.sams.disposition.SamsConsole.OffMagnetOn;
import jorgan.sams.disposition.SamsConsole.OnMagnetOff;
import jorgan.sams.disposition.SamsConsole.OnMagnetOn;
import jorgan.sams.disposition.SamsConsole.TabMessage;
import jorgan.sams.disposition.SamsConsole.TabOff;
import jorgan.sams.disposition.SamsConsole.TabOn;

/**
 */
public class SamsConsolePlayer extends ConsolePlayer<SamsConsole> {

	private PlayerContext context = new PlayerContext();

	private Tab[] tabs = new Tab[128];

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

	/**
	 * Overriden to let encoding decode change of tab.
	 */
	@Override
	public void send(MidiMessage message) {
		if (message instanceof ShortMessage) {
			decodeChangeTab(Arrays.asList(tabs), (ShortMessage) message);
		}
	}

	/**
	 * Overriden to let encoding decode tab changes.
	 */
	@Override
	protected void receive(MidiMessage message) {
		onReceived(message);
	}

	@Override
	protected void onInput(InputMessage message, Context context) {
		int tab = (int) context.get(TabMessage.TAB);

		if (tab >= 0 && tab < tabs.length) {
			if (message instanceof TabOn) {
				tabs[tab].onChanged(true);
			} else if (message instanceof TabOff) {
				tabs[tab].onChanged(false);
			}
		}
	}

	private void output(Class<? extends MagnetMessage> type, int tab) {
		context.set(MagnetMessage.TAB, tab);

		for (MagnetMessage message : getElement().getMessages(type)) {
			output(message, context);
		}
	}

	protected void onOutput(byte[] datas, Context context)
			throws InvalidMidiDataException {
		// let super implementation send the message
		super.send(MessageUtils.createMessage(datas));
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

		public void change(boolean on) {
			if (on) {
				offMagnet.off();
				onMagnet.on();
			} else {
				onMagnet.off();
				offMagnet.on();
			}
		}

		public void onChanged(boolean on) {
			SamsConsolePlayer.super.receive(encodeTabChanged(index, on));

			if (on) {
				onMagnet.off();
			} else {
				offMagnet.off();
			}
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

					if (onMagnet == this) {
						output(OnMagnetOn.class, index);
					} else {
						output(OffMagnetOn.class, index);
					}

					getOrganPlay().getClock().alarm(getElement(), offTime);
				}
			}

			public void off() {
				if (isOn()) {
					offTime = Long.MAX_VALUE;

					if (onMagnet == this) {
						output(OnMagnetOff.class, index);
					} else {
						output(OffMagnetOff.class, index);
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

	private void decodeChangeTab(List<Tab> tabs, ShortMessage message) {
		int index = message.getData1();

		if (message.getCommand() == ShortMessage.NOTE_ON) {
			tabs.get(index).change(true);
		} else if (message.getCommand() == ShortMessage.NOTE_OFF) {
			tabs.get(index).change(false);
		}
	}

	private ShortMessage encodeTabChanged(int index, boolean on) {
		return MessageUtils.newMessage(on ? ShortMessage.NOTE_ON
				: ShortMessage.NOTE_OFF, index, 127);
	}
}