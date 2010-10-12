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
package jorgan.play;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import jorgan.disposition.Element;
import jorgan.play.OrganPlay.Playing;

/**
 */
public class Clock {

	private OrganPlay play;

	private Thread thread;

	private TreeSet<Alarm> alarms = new TreeSet<Alarm>();

	public Clock(OrganPlay play) {
		this.play = play;

		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Clock.this.run();
			}
		}, "jOrgan Clock");
		thread.start();
	}

	private synchronized void run() {
		while (thread == Thread.currentThread()) {
			long now = System.currentTimeMillis();

			List<Alarm> pasts = new ArrayList<Alarm>();

			synchronized (alarms) {
				Iterator<Alarm> iterator = alarms.iterator();
				while (iterator.hasNext()) {
					Alarm alarm = iterator.next();
					if (alarm.getTime() <= now) {
						iterator.remove();
						pasts.add(alarm);
					} else {
						break;
					}
				}
			}

			trigger(pasts);

			synchronized (alarms) {
				try {
					if (alarms.isEmpty()) {
						alarms.wait();
					} else {
						alarms.wait(alarms.first().getTime() - now);
					}
				} catch (InterruptedException interrupted) {
				}
			}
		}
	}

	/**
	 * Trigger past alarms outside of synchronized block to prevent deadlocks
	 */
	private void trigger(List<Alarm> alarms) {
		for (Alarm past : alarms) {
			past.trigger();
		}
	}

	/**
	 * @see Player#onAlarm(long)
	 */
	public void alarm(final Element element, final long time) {
		alarm(element, new Playing() {
			@Override
			public void play(Player<?> player) {
				player.onAlarm(time);
			}
		}, time);
	}

	/**
	 * Alarm for the element at the given time.
	 */
	public void alarm(Element element, Playing playing, long time) {
		if (thread != null) {
			synchronized (alarms) {
				alarms.add(new Alarm(element, playing, time));
				alarms.notifyAll();
			}
		}
	}

	public void stop() {
		thread = null;

		synchronized (alarms) {
			alarms.clear();
			alarms.notifyAll();
		}
	}

	private class Alarm implements Comparable<Alarm> {

		private Element element;

		private Playing playing;

		private long time;

		public Alarm(Element element, Playing playing, long time) {
			this.element = element;
			this.playing = playing;
			this.time = time;
		}

		public long getTime() {
			return time;
		}

		public void trigger() {
			play.play(element, playing);
		}

		@Override
		public int compareTo(Alarm alarm) {
			// must never be equal, i.e. 0
			return (this.time < alarm.time) ? -1 : 1;
		}
	}
}