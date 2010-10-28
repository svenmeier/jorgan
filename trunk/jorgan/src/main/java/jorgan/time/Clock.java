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
package jorgan.time;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import jorgan.disposition.Organ;
import jorgan.time.spi.TimerRegistry;

/**
 */
public class Clock {

	private Thread thread;

	private List<Timer> timers;

	private TreeSet<Alarm> alarms = new TreeSet<Alarm>();

	public Clock(Organ organ) {
		timers = TimerRegistry.getTimers(organ, this);
	}

	/**
	 * Alarm for the element at the given time.
	 */
	public void alarm(WakeUp wakeUp, long time) {
		if (thread != null) {
			synchronized (alarms) {
				System.out.println(alarms.size());
				Iterator<Alarm> iterator = alarms.iterator();
				while (iterator.hasNext()) {
					Alarm alarm = iterator.next();
					if (wakeUp.replaces(alarm.wakeUp)) {
						iterator.remove();
					}
				}
				alarms.add(new Alarm(wakeUp, time));
				alarms.notifyAll();
			}
		}
	}

	public void start() {
		if (thread != null) {
			// already running
			return;
		}

		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Clock.this.run();
			}
		}, "jOrgan Clock");
		thread.start();

		for (Timer timer : timers) {
			timer.start();
		}
	}

	private void run() {
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

	public boolean isRunning() {
		return thread != null;
	}

	public void stop() {
		thread = null;

		synchronized (alarms) {
			alarms.clear();
			alarms.notifyAll();
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

	private class Alarm implements Comparable<Alarm> {

		private WakeUp wakeUp;

		private long time;

		public Alarm(WakeUp wakeUp, long time) {
			this.wakeUp = wakeUp;
			this.time = time;
		}

		public long getTime() {
			return time;
		}

		public void trigger() {
			wakeUp.trigger(time);
		}

		@Override
		public int compareTo(Alarm alarm) {
			// must never be equal, i.e. 0
			return (this.time < alarm.time) ? -1 : 1;
		}
	}
}