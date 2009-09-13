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

import jorgan.disposition.SwitchFilter;
import jorgan.disposition.Filter.Intercept;
import jorgan.disposition.SwitchFilter.Disengaged;
import jorgan.disposition.SwitchFilter.Engaged;
import jorgan.midi.mpl.Context;
import jorgan.play.sound.Channel;

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
			SwitchFilter filter = getElement();

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
	public void send(ShortMessage message, Context context) {
		if (context instanceof ChannelFilter) {
			((ChannelFilter) context).sendFilteredMessage(message.getCommand(),
					message.getData1(), message.getData2());
		} else {
			super.send(message, context);
		}
	}

	@Override
	protected void closeImpl() {
		super.closeImpl();

		channels.clear();
	}

	private class ChannelFilter extends PlayerContext implements Channel {

		private Channel channel;
		
		private boolean inited = false;

		public ChannelFilter(Channel channel) {
			this.channel = channel;

			channels.add(this);
		}

		public void init() {
			// no need if already done
			if (!inited) {
				SwitchFilter filter = getElement();
				if (filter.isEngaged()) {
					engaged();
				} else {
					disengaged();
				}
			}
			
			channel.init();
		}
		
		public void sendMessage(int command, int data1, int data2) {
			SwitchFilter element = getElement();

			boolean intercepted = false;

			for (Intercept message : element.getMessages(Intercept.class)) {
				if (process(message, command, data1, data2)) {
					intercepted = true;
				}
			}

			if (intercepted) {
				if (element.isEngaged()) {
					engaged();
				} else {
					disengaged();
				}
			} else {
				channel.sendMessage(command, data1, data2);
			}
		}

		private void engaged() {
			for (Engaged engaged : getElement().getMessages(Engaged.class)) {
				output(engaged, this);
			}
			
			inited = true;
		}

		private void disengaged() {
			for (Disengaged disengaged : getElement().getMessages(
					Disengaged.class)) {
				output(disengaged, this);
			}
			
			inited = true;
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