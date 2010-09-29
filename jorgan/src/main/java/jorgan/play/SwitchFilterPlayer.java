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

import jorgan.disposition.SwitchFilter;
import jorgan.disposition.SwitchFilter.Disengaged;
import jorgan.disposition.SwitchFilter.Engaged;
import jorgan.disposition.SwitchFilter.Intercept;
import jorgan.midi.mpl.Context;
import jorgan.play.sound.Channel;
import jorgan.util.Null;

/**
 * A player for an {@link SwitchFilter}.
 */
public class SwitchFilterPlayer extends SwitchPlayer<SwitchFilter> implements
		FilterPlayer {

	private List<ChannelFilter> channels = new ArrayList<ChannelFilter>();

	public SwitchFilterPlayer(SwitchFilter variation) {
		super(variation);
	}

	public Channel filter(Channel channel) {
		return new ChannelFilter(channel);
	}

	@Override
	public void update() {
		super.update();

		if (isOpen()) {
			for (ChannelFilter channel : channels) {
				channel.update();
			}
		}
	}

	@Override
	public void onOutput(byte[] datas, Context context)
			throws InvalidMidiDataException {
		if (context instanceof ChannelFilter) {
			((ChannelFilter) context).sendFilteredMessage(datas);
		} else {
			super.onOutput(datas, context);
		}
	}

	@Override
	protected void closeImpl() {
		super.closeImpl();

		channels.clear();
	}

	private class ChannelFilter extends PlayerContext implements Channel {

		private Channel channel;

		private Boolean engaged = null;

		public ChannelFilter(Channel channel) {
			this.channel = channel;

			channels.add(this);
		}

		@Override
		public void init() {
			update();

			channel.init();
		}

		protected void update() {
			boolean engaged = getElement().isEngaged();

			if (!Null.safeEquals(this.engaged, engaged)) {
				if (engaged) {
					engaged();
				} else {
					disengaged();
				}
				this.engaged = engaged;
			}
		}

		public void sendMessage(byte[] datas) throws InvalidMidiDataException {
			SwitchFilter element = getElement();

			boolean intercepted = false;

			for (Intercept message : element.getMessages(Intercept.class)) {
				if (process(message, datas)) {
					intercepted = true;
				}
			}

			if (intercepted) {
				boolean engaged = element.isEngaged();
				if (engaged) {
					engaged();
				} else {
					disengaged();
				}
				this.engaged = engaged;
			} else {
				channel.sendMessage(datas);
			}
		}

		private void engaged() {
			for (Engaged engaged : getElement().getMessages(Engaged.class)) {
				output(engaged, this);
			}
		}

		private void disengaged() {
			for (Disengaged disengaged : getElement().getMessages(
					Disengaged.class)) {
				output(disengaged, this);
			}
		}

		public void sendFilteredMessage(byte[] datas)
				throws InvalidMidiDataException {
			channel.sendMessage(datas);
		}

		public void release() {
			channel.release();

			channels.remove(this);
		}
	}
}