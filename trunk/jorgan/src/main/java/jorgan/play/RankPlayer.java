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

import javax.sound.midi.MidiUnavailableException;

import jorgan.disposition.MatcherException;
import jorgan.disposition.Rank;
import jorgan.disposition.Reference;
import jorgan.disposition.Rank.Muted;
import jorgan.disposition.Rank.Played;
import jorgan.disposition.Rank.Disengaged;
import jorgan.disposition.Rank.Engaged;
import jorgan.disposition.event.OrganEvent;
import jorgan.midi.channel.Channel;
import jorgan.midi.channel.ChannelFilter;
import jorgan.midi.channel.ChannelPool;
import jorgan.midi.channel.DelayedChannel;

/**
 * A player of a {@link jorgan.disposition.Rank}.
 */
public class RankPlayer extends Player<Rank> {

	private ChannelPool channelPool;

	private Channel channel;

	private int[] played = new int[128];

	private int totalNotes;

	public RankPlayer(Rank soundSource) {
		super(soundSource);
	}

	@Override
	protected void openImpl() {
		Rank rank = getElement();

		removeProblem(new Error("device"));
		if (rank.getDevice() != null) {
			try {
				channelPool = ChannelPool.instance(rank.getDevice());
				channelPool.open();
			} catch (MidiUnavailableException ex) {
				addProblem(new Error("device", rank.getDevice()));
			}
		}
	}

	@Override
	protected void closeImpl() {
		if (channel != null) {
			disengaged();
		}

		for (int n = 0; n < played.length; n++) {
			played[n] = 0;
		}
		totalNotes = 0;

		if (channelPool != null) {
			channelPool.close();
			channelPool = null;
		}
	}

	private void engaged() {
		Rank rank = getElement();

		channel = channelPool.createChannel(new RankChannelFilter());
		if (channel == null) {
			channel = new DeadChannel();

			addProblem(new Warning("channels", rank.getChannels().getPattern()));
		} else {
			for (Reference reference : rank.getReferences()) {
				SoundEffectPlayer effectPlayer = (SoundEffectPlayer) getOrganPlay()
						.getPlayer(reference.getElement());

				channel = effectPlayer.effectSound(channel);
			}

			if (rank.getDelay() > 0) {
				channel = new DelayedChannel(channel, rank.getDelay());
			}
		}

		for (Engaged engaged : getElement().getMessages(Engaged.class)) {
			output(engaged, channel);
		}
	}

	private void disengaged() {
		removeProblem(new Error("channels"));
		removeProblem(new Warning("channels"));

		for (Disengaged disengaged : getElement().getMessages(Disengaged.class)) {
			output(disengaged, channel);
		}

		channel.release();
		channel = null;
	}

	@Override
	public void elementChanged(OrganEvent event) {
		super.elementChanged(event);
		
		Rank rank = getElement();

		if (rank.getDevice() == null && getWarnDevice()) {
			addProblem(new Warning("device"));
		} else {
			removeProblem(new Warning("device"));
		}

		if (channelPool != null) {
			if (channel == null && rank.isEngaged()) {
				engaged();
			} else if (channel != null && !rank.isEngaged()) {
				disengaged();
			}
		}
	}

	public void play(int pitch, int velocity) {
		if (channel == null) {
			engaged();
		}

		if (played[pitch] == 0) {
			for (Played played : getElement().getMessages(Played.class)) {
				played.pitch = pitch;
				played.velocity = velocity;
				output(played, channel);
			}
		}
		played[pitch]++;

		totalNotes++;
	}

	public void mute(int pitch) {
		totalNotes--;

		played[pitch]--;
		if (played[pitch] == 0) {
			for (Muted muted : getElement().getMessages(Muted.class)) {
				muted.pitch = pitch;
				output(muted, channel);
			}
		}
	}

	private class DeadChannel implements Channel {
		public void sendMessage(int command, int data1, int data2) {
		}

		public void release() {
		}
	}

	private class RankChannelFilter implements ChannelFilter {
		public boolean accept(int channel) {
			try {
				Rank rank = getElement();

				return rank.getChannels().input(new int[] { channel });
			} catch (MatcherException ex) {
				addProblem(new Error("channels", ex.getPattern()));
			}
			return false;
		}
	}
}