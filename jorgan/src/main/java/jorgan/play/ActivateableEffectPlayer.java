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

import jorgan.disposition.ActivateableEffect;
import jorgan.disposition.event.OrganEvent;
import jorgan.midi.channel.Channel;
import jorgan.midi.channel.ChannelWrapper;

/**
 * A player for an {@link ActivateableEffect}.
 */
public class ActivateableEffectPlayer extends
		ActivateablePlayer<ActivateableEffect> implements SoundEffectPlayer {

	private List<ChannelImpl> channels = new ArrayList<ChannelImpl>();

	public ActivateableEffectPlayer(ActivateableEffect variation) {
		super(variation);
	}

	@Override
	protected void closeImpl() {
		super.closeImpl();

		channels.clear();
	}

	@Override
	public void elementChanged(OrganEvent event) {
		super.elementChanged(event);

		if (isOpen()) {
			for (ChannelImpl channel : channels) {
				channel.flush();
			}
		}
	}

	public Channel effectSound(Channel channel) {
		return new ChannelImpl(channel);
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

		@Override
		public void sendMessage(ShortMessage message) {
			// boolean effect = false;
			// if (command == CONTROL_CHANGE && data1 ==
			// CONTROL_BANK_SELECT_MSB) {
			// bank = data2;
			// effect = true;
			// }
			//
			// if (command == PROGRAM_CHANGE) {
			// program = data1;
			// effect = true;
			// }
			//
			// if (effect) {
			// flush();
			// } else {
			super.sendMessage(message);
			// }
		}

		private void flush() {
			// MomentaryEffect variation = getElement();
			//
			// int bank = this.bank;
			// int program = this.program;
			//
			// if (variation.isActivated()) {
			// bank += variation.getBank();
			// program += variation.getProgram();
			// }
			//
			// super.sendMessage(CONTROL_CHANGE, CONTROL_BANK_SELECT_MSB,
			// bank % 128);
			//
			// super.sendMessage(PROGRAM_CHANGE, program % 128, UNUSED_DATA);
		}
	}
}