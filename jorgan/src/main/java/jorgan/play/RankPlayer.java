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

import javax.sound.midi.InvalidMidiDataException;

import jorgan.disposition.Element;
import jorgan.disposition.Filter;
import jorgan.disposition.Rank;
import jorgan.disposition.Sound;
import jorgan.disposition.Rank.Disengaged;
import jorgan.disposition.Rank.Engaged;
import jorgan.disposition.Rank.NoteMuted;
import jorgan.disposition.Rank.NotePlayed;
import jorgan.midi.mpl.Command;
import jorgan.midi.mpl.Context;
import jorgan.midi.mpl.ProcessingException;
import jorgan.play.OrganPlay.Playing;
import jorgan.play.sound.Channel;
import jorgan.play.sound.ChannelFilter;
import jorgan.problem.Severity;

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
		try {
			for (Sound sound : rank.getReferenced(Sound.class)) {
				SoundPlayer<?> player = (SoundPlayer<?>) getPlayer(sound);

				// sound might not have player
				if (player != null) {
					channel = player.createChannel(new RankChannelFilter(rank
							.getChannel()));
					break;
				}
			}
		} catch (ProcessingException ex) {
			channel = new DeadChannel();

			addProblem(Severity.ERROR, "channel", "channelIllegal", rank
					.getChannel());
			return;
		}

		if (rank.getDelay() > 0) {
			channel = new DelayedChannel(channel);
		}

		if (channel == null) {
			channel = new DeadChannel();

			addProblem(Severity.WARNING, "channel", "channelUnavailable", rank
					.getChannel());
		}

		for (Element element : rank.getReferenced(Element.class)) {
			if (element instanceof Filter) {
				FilterPlayer player = (FilterPlayer) getPlayer(element);

				channel = player.filter(channel);
			}
		}

		this.channel = new ChannelImpl(channel);
		this.channel.init();
	}

	private void disengaged() {
		this.channel.disengaged();
		this.channel.release();
		this.channel = null;

		removeProblem(Severity.WARNING, "channel");
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
			set(NotePlayed.PITCH, pitch);
			set(NotePlayed.VELOCITY, velocity);
			for (NotePlayed notePlayed : getElement().getMessages(
					NotePlayed.class)) {
				output(notePlayed, this);
			}
		}

		private void muted(int pitch) {
			set(NoteMuted.PITCH, pitch);
			for (NoteMuted noteMuted : getElement()
					.getMessages(NoteMuted.class)) {
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

	private class RankChannelFilter implements ChannelFilter, Context {

		private Command[] commands;

		public RankChannelFilter(String pattern) throws ProcessingException {
			this.commands = Command.parse(pattern);
		}

		public boolean accept(int channel) {
			return !Float.isNaN(commands[0].process(channel, this));
		}

		public float get(String name) {
			throw new UnsupportedOperationException();
		}

		public void set(String name, float value) {
			throw new UnsupportedOperationException();
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
		public void sendMessage(final byte[] datas) {
			Rank rank = getElement();

			getOrganPlay().getClock().alarm(rank, new Playing() {
				@Override
				public void play(Player<?> player) {
					try {
						channel.sendMessage(datas);
					} catch (InvalidMidiDataException nothingWeCanDoAboutIt) {
					}
				}
			}, System.currentTimeMillis() + rank.getDelay());
		}
	}
}