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
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

import jorgan.disposition.Output;
import jorgan.play.output.Channel;
import jorgan.play.output.ChannelFilter;

/**
 * A player of {@link jorgan.disposition.Output} subclasses.
 */
public abstract class OutputPlayer<E extends Output> extends Player<E> {

	private static final int MAX_CHANNELS = 16;

	/**
	 * Created channels.
	 */
	private List<ChannelImpl> channels = new ArrayList<ChannelImpl>();

	public OutputPlayer(E output) {
		super(output);

		for (int c = 0; c < MAX_CHANNELS; c++) {
			channels.add(null);
		}
	}

	/**
	 * Send a message to this output.
	 * 
	 * @param message
	 *            message sent
	 */
	public abstract void send(ShortMessage message);

	/**
	 * Create a channel.
	 * 
	 * @return created channel or <code>null</code> if no channel is available
	 */
	public Channel createChannel(ChannelFilter filter) {
		for (int c = 0; c < channels.size(); c++) {
			if (channels.get(c) == null && filter.accept(c)) {
				return new ChannelImpl(c);
			}
		}

		return null;
	}

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

		public int getNumber() {
			return channel + 1;
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
		public void sendMessage(ShortMessage message) {
			try {
				message.setMessage(message.getCommand(), channel, message
						.getData1(), message.getData2());
			} catch (InvalidMidiDataException ex) {
				throw new Error(ex);
			}

			send(message);
		}
	}
}