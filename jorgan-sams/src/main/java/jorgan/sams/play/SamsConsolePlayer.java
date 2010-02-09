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

import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

import jorgan.play.ConsolePlayer;
import jorgan.sams.disposition.SamsConsole;

/**
 */
public class SamsConsolePlayer extends ConsolePlayer<SamsConsole> {

	private Tab[] tabs = new Tab[128];

	private Thread durationThread;

	public SamsConsolePlayer(SamsConsole console) {
		super(console);

		for (int t = 0; t < tabs.length; t++) {
			tabs[t] = new Tab(t);
		}
	}

	@Override
	protected synchronized void openImpl() {
		super.openImpl();

		durationThread = new Thread(new Runnable() {
			@Override
			public void run() {
				checkDuration();
			}
		}, "jOrgan Magnets");
		durationThread.start();
	}

	@Override
	protected synchronized void closeImpl() {
		if (durationThread != null) {
			durationThread = null;
			notifyAll();
		}

		for (Tab tab : tabs) {
			tab.reset();
		}

		super.closeImpl();
	}

	public synchronized void durationUpdate() {
		notify();
	}

	private synchronized void checkDuration() {
		while (durationThread == Thread.currentThread()) {
			long now = System.currentTimeMillis();
			long then = checkDuration(now);
			try {
				wait(Math.max(0, then - now));
			} catch (InterruptedException interrupted) {
			}
		}
	}

	protected long checkDuration(long now) {
		long then = Long.MAX_VALUE;

		for (Tab tab : tabs) {
			then = Math.min(tab.checkDuration(now), then);
		}

		return then;
	}

	/**
	 * Overriden to let encoding decode change of tab.
	 */
	@Override
	public synchronized void send(MidiMessage message) {
		if (message instanceof ShortMessage) {
			getElement().getEncoding().decodeChangeTab(Arrays.asList(tabs),
					(ShortMessage) message);
		}
	}

	/**
	 * Overriden to let encoding decode tab changes.
	 */
	@Override
	protected synchronized void receive(MidiMessage message) {
		if (message instanceof ShortMessage) {
			ShortMessage shortMessage = (ShortMessage) message;

			getElement().getEncoding().decodeTabChanged(Arrays.asList(tabs),
					shortMessage);
		}
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

		public long checkDuration(long time) {
			return Math.min(onMagnet.checkDuration(time), offMagnet
					.checkDuration(time));
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
			if (on) {
				SamsConsolePlayer.super.receive(getElement().getEncoding()
						.encodeTabChanged(index, on));

				onMagnet.off();
			} else {
				SamsConsolePlayer.super.receive(getElement().getEncoding()
						.encodeTabChanged(index, on));

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
					durationUpdate();

					ShortMessage message;
					if (onMagnet == this) {
						message = getElement().getEncoding().encodeOnMagnet(
								index, true);
					} else {
						message = getElement().getEncoding().encodeOffMagnet(
								index, true);
					}
					SamsConsolePlayer.super.send(message);
				}
			}

			public void off() {
				if (isOn()) {
					offTime = Long.MAX_VALUE;

					ShortMessage message;
					if (onMagnet == this) {
						message = getElement().getEncoding().encodeOnMagnet(
								index, false);
					} else {
						message = getElement().getEncoding().encodeOffMagnet(
								index, false);
					}
					SamsConsolePlayer.super.send(message);
				}
			}

			public long checkDuration(long time) {
				if (isOn()) {
					if (offTime <= time) {
						off();
					}
				}

				return offTime;
			}
		}
	}
}