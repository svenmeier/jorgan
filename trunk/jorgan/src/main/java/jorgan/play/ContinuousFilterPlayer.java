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
import jorgan.disposition.Message.InputMessage;
import jorgan.disposition.Filter.Intercept;
import jorgan.disposition.event.OrganEvent;
import jorgan.midi.channel.Channel;
import jorgan.util.math.ProcessingException;
import jorgan.util.math.NumberProcessor.Context;

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
		channel = new ChannelFilter(channel);

		// TODO would be sufficient to handle changing on new channel only
		engaging();

		return channel;
	}

	@Override
	protected void closeImpl() {
		channels.clear();
	}

	@Override
	public void elementChanged(OrganEvent event) {
		super.elementChanged(event);

		if (isOpen()) {
			engaging();
		}
	}

	private void engaging() {
		ContinuousFilter filter = getElement();

		for (Engaging engaging : getElement().getMessages(Engaging.class)) {
			for (ChannelFilter channel : channels) {
				channel.set(Engaging.VALUE, filter.getValue());
				output(engaging, channel);
			}
		}
	}

	@Override
	protected void input(InputMessage message, Context context)
			throws ProcessingException {
		if (message instanceof Intercept) {
			engaging();
		} else {
			super.input(message, context);
		}
	}

	@Override
	protected void output(ShortMessage message, Context context) {
		((ChannelFilter) context).sendFilteredMessage(message);
	}

	private class ChannelFilter extends PlayerContext implements Channel {

		private Channel channel;

		public ChannelFilter(Channel channel) {
			this.channel = channel;

			channels.add(this);
		}

		public void sendMessage(ShortMessage message) {
			if (!input(message, Intercept.class, this)) {
				channel.sendMessage(message);
			}
		}

		public void sendFilteredMessage(ShortMessage message) {
			channel.sendMessage(message);
		}

		public void release() {
			channel.release();

			channels.remove(this);
		}
	}
}