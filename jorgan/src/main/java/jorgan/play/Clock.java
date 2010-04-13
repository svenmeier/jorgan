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

			// trigger past alarms outside of synchronized block to prevent
			// deadlock
			for (Alarm past : pasts) {
				past.trigger();
			}

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
	 * Alarm for the element at the given time.
	 */
	public void alarm(Element element, long time) {

		synchronized (alarms) {
			alarms.add(new Alarm(element, time));
			alarms.notifyAll();
		}
	}

	public void destroy() {
		thread = null;

		synchronized (alarms) {
			alarms.clear();
			alarms.notifyAll();
		}
	}

	private class Alarm implements Comparable<Alarm>, Playing {

		private Element element;

		private long time;

		public Alarm(Element element, long time) {
			this.element = element;
			this.time = time;
		}

		public long getTime() {
			return time;
		}

		public void trigger() {
			play.play(element, this);
		}

		@Override
		public void play(Player<?> player) {
			player.onAlarm(time);
		}

		@Override
		public int compareTo(Alarm alarm) {
			return (this.time < alarm.time) ? -1 : 1;
		}
	}
}