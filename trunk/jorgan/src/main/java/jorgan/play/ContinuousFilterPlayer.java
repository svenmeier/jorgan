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

import javax.sound.midi.ShortMessage;

import jorgan.disposition.ContinuousFilter;
import jorgan.disposition.ContinuousFilter.Engaging;
import jorgan.disposition.Filter.Intercept;
import jorgan.disposition.event.OrganEvent;
import jorgan.midi.mpl.Context;
import jorgan.play.sound.Channel;

/**
 * A player for a swell.
 */
public class ContinuousFilterPlayer extends ContinuousPlayer<ContinuousFilter>
		implements FilterPlayer {

	private List<ChannelFilter> channels = new ArrayList<ChannelFilter>();

	public ContinuousFilterPlayer(ContinuousFilter swell) {
		super(swell);
	}

	public Channel filter(Channel channel) {
		ChannelFilter channelFilter = new ChannelFilter(channel);

		channelFilter.engaging();

		return channelFilter;
	}

	@Override
	protected void closeImpl() {
		channels.clear();
	}

	@Override
	public void elementChanged(OrganEvent event) {
		super.elementChanged(event);

		if (isOpen()) {
			for (ChannelFilter channel : channels) {
				channel.engaging();
			}
		}
	}

	@Override
	public void send(ShortMessage message, Context context) {
		if (context instanceof ChannelFilter) {
			((ChannelFilter) context).sendFilteredMessage(message.getCommand(),
					message.getData1(), message.getData2());
		} else {
			super.send(message, context);
		}
	}

	private class ChannelFilter extends PlayerContext implements Channel {

		private Channel channel;

		public ChannelFilter(Channel channel) {
			this.channel = channel;

			channels.add(this);
		}

		public void sendMessage(int command, int data1, int data2) {
			ContinuousFilter element = getElement();

			boolean filtered = false;

			for (Intercept message : element.getMessages(Intercept.class)) {
				if (process(message, command, data1, data2)) {
					filtered = true;
				}
			}

			if (filtered) {
				engaging();
			} else {
				channel.sendMessage(command, data1, data2);
			}
		}

		private void engaging() {
			ContinuousFilter filter = getElement();

			for (Engaging engaging : getElement().getMessages(Engaging.class)) {
				set(Engaging.VALUE, filter.getValue());
				output(engaging, this);
			}
		}

		public void sendFilteredMessage(int command, int data1, int data2) {
			channel.sendMessage(command, data1, data2);
		}

		public void release() {
			channel.release();

			channels.remove(this);
		}
	}
}