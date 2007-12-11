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

import jorgan.disposition.ActivateableFilter;
import jorgan.disposition.ActivateableFilter.Disengaged;
import jorgan.disposition.ActivateableFilter.Engaged;
import jorgan.disposition.Filter.Intercept;
import jorgan.disposition.event.OrganEvent;
import jorgan.midi.channel.Channel;
import jorgan.midi.mpl.Processor.Context;

/**
 * A player for an {@link ActivateableFilter}.
 */
public class ActivateableFilterPlayer extends
		ActivateablePlayer<ActivateableFilter> implements FilterPlayer {

	private List<ChannelFilter> channels = new ArrayList<ChannelFilter>();

	public ActivateableFilterPlayer(ActivateableFilter variation) {
		super(variation);
	}

	public Channel filter(Channel channel) {
		ChannelFilter channelFilter = new ChannelFilter(channel);

		ActivateableFilter filter = getElement();
		if (filter.isEngaged()) {
			channelFilter.engaged();
		} else {
			channelFilter.disengaged();
		}

		return channelFilter;
	}

	@Override
	public void elementChanged(OrganEvent event) {
		super.elementChanged(event);

		if (isOpen()) {
			ActivateableFilter filter = getElement();

			for (ChannelFilter channel : channels) {
				if (filter.isEngaged()) {
					channel.engaged();
				} else {
					channel.disengaged();
				}
			}
		}
	}

	@Override
	public void output(ShortMessage message, Context context) {
		if (context instanceof ChannelFilter) {
			((ChannelFilter) context).sendFilteredMessage(message);
		} else {
			super.output(message, context);
		}
	}

	@Override
	protected void closeImpl() {
		super.closeImpl();

		channels.clear();
	}

	private class ChannelFilter extends PlayerContext implements Channel {

		private Channel channel;

		public ChannelFilter(Channel channel) {
			this.channel = channel;

			channels.add(this);
		}

		public void sendMessage(ShortMessage shortMessage) {
			ActivateableFilter element = getElement();

			boolean filtered = false;

			for (Intercept message : element.getMessages(Intercept.class)) {
				if (process(shortMessage, message, this)) {
					if (element.isEngaged()) {
						engaged();
					} else {
						disengaged();
					}
					filtered = true;
				}
			}

			if (!filtered) {
				channel.sendMessage(shortMessage);
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

		public void sendFilteredMessage(ShortMessage message) {
			channel.sendMessage(message);
		}

		public void release() {
			channel.release();

			channels.remove(this);
		}
	}
}