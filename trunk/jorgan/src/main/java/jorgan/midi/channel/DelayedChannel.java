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
package jorgan.midi.channel;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A delayed channel.
 */
public class DelayedChannel implements Channel {

	private static final Logger logger = Logger.getLogger(DelayedChannel.class
			.getName());

	private static DelayedInvoker invoker = new DelayedInvoker();

	private int delay;

	private Channel channel;

	/**
	 * Create a delayed channel.
	 * 
	 * @param channel
	 *            channel to wrap
	 * @param delay
	 *            the delay
	 */
	public DelayedChannel(Channel channel, int delay) {
		this.channel = channel;

		this.delay = delay;
	}

	public void sendMessage(final int command, final int data1, final int data2) {
		new DelayedInvocation() {
			@Override
			public void now() {
				if (channel != null) {
					channel.sendMessage(command, data1, data2);
				}
			}
		};
	}

	public void release() {
		channel.release();
		channel = null;
	}

	private abstract class DelayedInvocation implements Comparable {

		private long when;

		public DelayedInvocation() {
			when = System.currentTimeMillis() + delay;

			invoker.delay(this);
		}

		public int compareTo(Object object) {
			DelayedInvocation invocation = (DelayedInvocation) object;

			if (this.when < invocation.when) {
				return -1;
			} else if (this.when > invocation.when) {
				return 1;
			}
			return 0;
		}

		public abstract void now();
	}

	private static class DelayedInvoker implements Runnable {

		private SortedSet<DelayedInvocation> invocations = new TreeSet<DelayedInvocation>();

		public DelayedInvoker() {
			Thread thread = new Thread(this, "DelayedChannelInvoker");
			thread.setDaemon(true);
			thread.start();
		}

		/**
		 * Schedule an invocation for delayed invocation.
		 * 
		 * @param invocation
		 *            invocation to schedule
		 */
		public synchronized void delay(DelayedInvocation invocation) {
			invocations.add(invocation);
			notify();
		}

		public synchronized void run() {
			while (true) {
				try {
					if (invocations.size() == 0) {
						wait();
					} else {
						DelayedInvocation invocation = invocations.first();

						long timeout = invocation.when
								- System.currentTimeMillis();
						if (timeout <= 0) {
							invocations.remove(invocation);

							try {
								invocation.now();
							} catch (RuntimeException ex) {
								logger.log(Level.WARNING, "invocation failed",
										ex);
							}
						} else {
							wait(timeout);
						}
					}
				} catch (InterruptedException ex) {
					throw new Error("unexpected interruption", ex);
				}
			}
		}
	}
}
