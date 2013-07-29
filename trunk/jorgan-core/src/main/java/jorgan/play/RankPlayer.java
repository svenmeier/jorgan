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

import java.util.Arrays;

import javax.sound.midi.InvalidMidiDataException;

import jorgan.disposition.Element;
import jorgan.disposition.Rank;
import jorgan.disposition.Rank.Disengaged;
import jorgan.disposition.Rank.Engaged;
import jorgan.disposition.Rank.NoteMuted;
import jorgan.disposition.Rank.NotePlayed;
import jorgan.disposition.Sound;
import jorgan.disposition.SoundFilter;
import jorgan.midi.mpl.Context;
import jorgan.play.sound.Channel;
import jorgan.problem.Severity;
import jorgan.time.WakeUp;

/**
 * A player of a {@link jorgan.disposition.Rank}.
 */
public class RankPlayer extends Player<Rank> {

	private ChannelImpl channel;

	private int[] played = new int[128];

	public RankPlayer(Rank soundSource) {
		super(soundSource);
	}

	@Override
	protected void openImpl() {
		removeProblem(Severity.WARNING, "channel");
		removeProblem(Severity.ERROR, "channel");
	}

	@Override
	protected void closeImpl() {
		if (channel != null) {
			disengaged();
		}
	}

	private void engaged() {
		Rank rank = getElement();

		for (int n = 0; n < played.length; n++) {
			played[n] = 0;
		}

		Channel channel = null;
		for (Sound sound : rank.getReferenced(Sound.class)) {
			SoundPlayer<?> player = (SoundPlayer<?>) getPlayer(sound);

			// sound might not have player
			if (player != null) {
				channel = player.createChannel(rank.getChannel());
				break;
			}
		}

		if (rank.getDelay() > 0) {
			channel = new DelayedChannel(channel);
		}

		if (channel == null) {
			channel = new DeadChannel();

			addProblem(Severity.WARNING, "channel", "channelUnavailable",
					rank.getChannel());
		}

		for (Element element : rank.getReferenced(Element.class)) {
			if (element instanceof SoundFilter) {
				FilterPlayer player = (FilterPlayer) getPlayer(element);

				channel = player.filter(channel);
			}
		}

		this.channel = new ChannelImpl(channel);
		this.channel.init();
	}

	private void disengaged() {
		if (this.channel != null) {
			removeProblem(Severity.WARNING, "channel");

			this.channel.disengaged();
			this.channel.release();
			this.channel = null;
		}
	}

	@Override
	protected void onOutput(byte[] datas, Context context)
			throws InvalidMidiDataException {
		channel.sendMessage(datas);
	}

	@Override
	public void update() {
		super.update();

		Rank rank = getElement();

		if (isOpen()) {
			if (channel == null && rank.isEngaged()) {
				engaged();
			} else if (channel != null && !rank.isEngaged()) {
				disengaged();
			}
		}
	}

	public void play(int pitch, int velocity) {
		if (isOpen()) {
			if (channel == null) {
				engaged();
			}

			if (played[pitch] == 0) {
				channel.played(pitch, velocity);
			}
			played[pitch]++;
		}
	}

	public void mute(int pitch) {
		if (isOpen()) {
			if (channel == null) {
				return;
			}

			played[pitch]--;
			if (played[pitch] == 0) {
				channel.muted(pitch);
			}
		}
	}

	public void repeat(int velocity) {
		if (this.channel != null) {
			for (int p = 0; p < played.length; p++) {
				if (played[p] > 0) {
					this.channel.muted(p);
				}
			}

			for (int p = 0; p < played.length; p++) {
				if (played[p] > 0) {
					this.channel.played(p, velocity);
				}
			}
		}
	}

	private class ChannelImpl extends PlayerContext implements Channel {
		private Channel channel;

		public ChannelImpl(Channel channel) {
			this.channel = channel;
		}

		public void init() {
			engaged();

			channel.init();
		}

		public void engaged() {
			for (Engaged engaged : getElement().getMessages(Engaged.class)) {
				output(engaged, this);
			}
		}

		public void disengaged() {
			for (Disengaged disengaged : getElement().getMessages(
					Disengaged.class)) {
				output(disengaged, this);
			}
		}

		public void sendMessage(byte[] datas) throws InvalidMidiDataException {
			channel.sendMessage(datas);
		}

		public void release() {
			channel.release();
		}

		private void played(int pitch, int velocity) {
			for (NotePlayed notePlayed : getElement().getMessages(
					NotePlayed.class)) {
				set(NotePlayed.PITCH, pitch);
				set(NotePlayed.VELOCITY, velocity);
				output(notePlayed, this);
			}
		}

		private void muted(int pitch) {
			for (NoteMuted noteMuted : getElement()
					.getMessages(NoteMuted.class)) {
				set(NoteMuted.PITCH, pitch);
				output(noteMuted, this);
			}
		}
	}

	private class DeadChannel implements Channel {
		public void init() {
		}

		public void sendMessage(byte[] datas) {
		}

		public void release() {
		}
	}

	private class DelayedChannel implements Channel {

		private Channel channel;

		public DelayedChannel(Channel channel) {
			this.channel = channel;
		}

		@Override
		public void init() {
			this.channel.init();
		}

		@Override
		public void release() {
			this.channel.release();
		}

		@Override
		public void sendMessage(byte[] datas) {
			Rank rank = getElement();

			int delay = rank.getDelay();
			final byte[] copy = Arrays.copyOf(datas, datas.length);

			getOrganPlay().alarm(new WakeUp() {

				@Override
				public boolean replaces(WakeUp wakeUp) {
					return false;
				}

				@Override
				public void trigger() {
					try {
						channel.sendMessage(copy);
					} catch (InvalidMidiDataException nothingWeCanDoAboutIt) {
					}
				}
			}, delay);
		}
	}
}