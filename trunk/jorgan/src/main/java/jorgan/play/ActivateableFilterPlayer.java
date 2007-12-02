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
import jorgan.disposition.Message.InputMessage;
import jorgan.disposition.event.OrganEvent;
import jorgan.midi.channel.Channel;
import jorgan.midi.mpl.ProcessingException;
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
		channel = new ChannelFilter(channel);

		// TODO would be sufficient to handle engaged/disengaged on new channel
		// only
		ActivateableFilter filter = getElement();
		if (filter.isEngaged()) {
			engaged();
		} else {
			disengaged();
		}

		return channel;
	}

	@Override
	public void elementChanged(OrganEvent event) {
		super.elementChanged(event);

		if (isOpen()) {
			ActivateableFilter filter = getElement();
			if (filter.isEngaged()) {
				engaged();
			} else {
				disengaged();
			}
		}
	}

	private void engaged() {
		for (Engaged engaged : getElement().getMessages(Engaged.class)) {
			for (ChannelFilter channel : channels) {
				output(engaged, channel);
			}
		}
	}

	private void disengaged() {
		for (Disengaged disengaged : getElement().getMessages(Disengaged.class)) {
			for (ChannelFilter channel : channels) {
				output(disengaged, channel);
			}
		}
	}

	@Override
	protected void input(InputMessage message, Context context)
			throws ProcessingException {
		if (message instanceof Intercept) {
			ActivateableFilter filter = getElement();
			if (filter.isEngaged()) {
				engaged();
			} else {
				disengaged();
			}
		} else {
			super.input(message, context);
		}
	}

	@Override
	protected void output(ShortMessage message, Context context) {
		((ChannelFilter) context).sendFilteredMessage(message);
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