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

import jorgan.disposition.Sound;
import jorgan.play.sound.Channel;
import jorgan.play.sound.ChannelFilter;

/**
 * A player of {@link jorgan.disposition.Sound} subclasses.
 */
public abstract class SoundPlayer<E extends Sound> extends Player<E> {

	/**
	 * Created channels.
	 */
	private ArrayList<ChannelImpl> channels = new ArrayList<ChannelImpl>();

	protected SoundPlayer(E sound) {
		super(sound);
	}

	protected int getChannelCount() {
		return 16;
	}

	/**
	 * Create a channel.
	 * 
	 * @return created channel or <code>null</code> if no channel is available
	 */
	public Channel createChannel(ChannelFilter filter) {
		for (int c = 0; c < getChannelCount(); c++) {
			if (filter.accept(c)) {
				while (channels.size() <= c) {
					channels.add(null);
				}

				if (channels.get(c) == null) {
					return new ChannelImpl(c);
				}
			}
		}

		return null;
	}

	protected abstract boolean send(int channel, int command, int data1,
			int data2);

	/**
	 * A channel implementation.
	 */
	private class ChannelImpl implements Channel {

		/**
		 * The MIDI channel of this sound.
		 */
		private int channel;

		/**
		 * Create a channel.
		 * 
		 * @param channel
		 *            the channel to use
		 */
		public ChannelImpl(int channel) {
			this.channel = channel;

			channels.set(channel, this);
		}

		/**
		 * Release.
		 */
		public void release() {
			channels.set(channel, null);
		}

		/**
		 * Send a message.
		 * 
		 * @param message
		 *            message
		 */
		public void sendMessage(int command, int data1, int data2) {
			if (send(channel, command, data1, data2)) {
				if (getOrganPlay() != null) {
					getOrganPlay().fireOutputProduced(channel, command, data1,
							data2);
				}
			}
		}
	}
}