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

import javax.sound.midi.InvalidMidiDataException;

import jorgan.disposition.Sound;
import jorgan.midi.mpl.Command;
import jorgan.midi.mpl.Context;
import jorgan.midi.mpl.NoOp;
import jorgan.play.sound.Channel;

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
	public Channel createChannel(Command command) {
		if (command instanceof NoOp) {
			return new ChannelImpl(-1);
		} else {
			Context context = new Context() {
				public float get(String name) {
					return Float.NaN;
				}

				public void set(String name, float value) {
				}
			};

			for (int c = 0; c < getChannelCount(); c++) {
				if (!Float.isNaN(command.process(c, context))) {
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
	}

	protected abstract void send(int channel, byte[] datas)
			throws InvalidMidiDataException;

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

			if (channel != -1) {
				channels.set(channel, this);
			}
		}

		public void init() {
		}

		/**
		 * Release.
		 */
		public void release() {
			if (channel != -1) {
				channels.set(channel, null);
			}
		}

		/**
		 * Send a message.
		 * 
		 * @param message
		 *            message
		 * @throws InvalidMidiDataException
		 */
		public void sendMessage(byte[] datas) throws InvalidMidiDataException {
			send(channel, datas);
		}
	}
}