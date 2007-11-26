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
import java.util.Map;

import javax.sound.midi.ShortMessage;

import jorgan.disposition.ActivateableEffect;
import jorgan.disposition.ActivateableEffect.Disengaged;
import jorgan.disposition.ActivateableEffect.Engaged;
import jorgan.disposition.event.OrganEvent;
import jorgan.midi.channel.Channel;
import jorgan.midi.channel.ChannelWrapper;

/**
 * A player for an {@link ActivateableEffect}.
 */
public class ActivateableEffectPlayer extends
		ActivateablePlayer<ActivateableEffect> implements SoundEffectPlayer {

	private List<ChannelImpl> channels = new ArrayList<ChannelImpl>();

	private transient Channel currentChannel;

	public ActivateableEffectPlayer(ActivateableEffect variation) {
		super(variation);
	}

	public Channel effectSound(Channel channel) {
		channel = new ChannelImpl(channel);

		// TODO would be sufficient to handle engaged/disengaged on new channel
		// only
		ActivateableEffect effect = getElement();
		if (effect.isEngaged()) {
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
			ActivateableEffect effect = getElement();
			if (effect.isEngaged()) {
				engaged();
			} else {
				disengaged();
			}
		}
	}

	private void engaged() {
		for (Engaged engaged : getElement().getMessages(Engaged.class)) {
			for (Channel channel : channels) {
				Map<String, Float> values = getValues();

				currentChannel = channel;
				output(engaged, values);
			}
		}
	}

	private void disengaged() {
		for (Disengaged disengaged : getElement().getMessages(Disengaged.class)) {
			for (Channel channel : channels) {
				Map<String, Float> values = getValues();

				currentChannel = channel;
				output(disengaged, values);
			}
		}
	}

	@Override
	protected void output(ShortMessage message) {
		currentChannel.sendMessage(message);
	}

	@Override
	protected void closeImpl() {
		super.closeImpl();

		channels.clear();
	}

	private class ChannelImpl extends ChannelWrapper {

		public ChannelImpl(Channel channel) {
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