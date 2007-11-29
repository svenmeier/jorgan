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

import jorgan.disposition.ContinuousEffect;
import jorgan.disposition.ContinuousEffect.Engaging;
import jorgan.disposition.Message.InputMessage;
import jorgan.disposition.SoundEffect.Effect;
import jorgan.disposition.event.OrganEvent;
import jorgan.midi.channel.Channel;
import jorgan.midi.channel.ChannelWrapper;
import jorgan.util.math.ProcessingException;

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
		ContinuousEffect effect = getElement();

		for (Engaging engaging : getElement().getMessages(Engaging.class)) {
			setParameter(Engaging.VALUE, effect.getValue());
			for (Channel channel : channels) {
				currentChannel = channel;
				output(engaging);
			}
		}
	}

	@Override
	protected void input(InputMessage message) throws ProcessingException {
		if (message instanceof Effect) {
			engaging();
		} else {
			super.input(message);
		}
	}
	
	@Override
	protected void output(ShortMessage message) {
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