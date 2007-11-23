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

import jorgan.disposition.ContinuousEffect;
import jorgan.disposition.ContinuousEffect.Changing;
import jorgan.disposition.event.OrganEvent;
import jorgan.midi.channel.Channel;
import jorgan.midi.channel.ChannelWrapper;

/**
 * A player for a swell.
 */
public class ContinuousEffectPlayer extends ContinuousPlayer<ContinuousEffect>
		implements SoundEffectPlayer {

	private List<ChannelImpl> channels = new ArrayList<ChannelImpl>();

	private transient Channel currentChannel;
	
	public ContinuousEffectPlayer(ContinuousEffect swell) {
		super(swell);
	}

	public Channel effectSound(Channel channel) {
		channel = new ChannelImpl(channel);
		
		// TODO would be sufficient to handle changing on new channel only
		changing();
		
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
			changing();
		}
	}

	private void changing() {
		ContinuousEffect effect = getElement();

		for (Changing changing : getElement().getMessages(Changing.class)) {
			changing.value = effect.getValue();
			for (Channel channel : channels) {
				currentChannel = channel;
				output(changing);
			}
		}
	}
	
	@Override
	protected void output(int status, int data1, int data2)
			throws InvalidMidiDataException {
		ShortMessage message = new ShortMessage();
		message.setMessage(status, data1, data2);

		currentChannel.sendMessage(message);
	}

	private class ChannelImpl extends ChannelWrapper {

		private ChannelImpl(Channel channel) {
			super(channel);

			channels.add(this);
		}

		@Override
		public void release() {
			super.release();

			channels.remove(this);
		}
	}
}