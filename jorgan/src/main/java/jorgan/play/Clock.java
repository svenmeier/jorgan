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

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import jorgan.disposition.Element;
import jorgan.play.OrganPlay.Playing;

/**
 */
public class Clock {

	private OrganPlay play;

	private Thread thread;

	private Set<Alarm> alarms = new TreeSet<Alarm>();

	public Clock(OrganPlay play) {
		this.play = play;

		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Clock.this.run();
			}
		}, "jOrgan Alarm");
		thread.start();
	}

	private synchronized void run() {
		while (thread == Thread.currentThread()) {
			long time = System.currentTimeMillis();

			long next = Long.MAX_VALUE;

			Iterator<Alarm> iterator = alarms.iterator();
			while (iterator.hasNext()) {
				Alarm alarm = iterator.next();
				if (alarm.check(time)) {
					iterator.remove();
				} else {
					next = alarm.getTime();
					break;
				}
			}

			try {
				if (next == Long.MAX_VALUE) {
					wait();
				} else {
					wait(next - time);
				}
			} catch (InterruptedException interrupted) {
			}
		}
	}

	/**
	 * Alarm for the element at the given time.
	 */
	public synchronized void alarm(Element element, long time) {
		alarms.add(new Alarm(element, time));

		notifyAll();
	}

	public synchronized void destroy() {
		thread = null;

		alarms.clear();

		notifyAll();
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

		public boolean check(long time) {
			if (time >= this.time) {
				play.play(element, this);

				return true;
			} else {
				return false;
			}
		}

		@Override
		public void play(Player<?> player) {
			player.onAlarm(time);
		}

		@Override
		public int compareTo(Alarm alarm) {
			boolean first = this.time - alarm.time < 0;

			return first ? -1 : 1;
		}
	}
}