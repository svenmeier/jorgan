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
import javax.sound.midi.ShortMessage;

import jorgan.disposition.Rank;
import jorgan.disposition.Reference;
import jorgan.disposition.Rank.Disengaged;
import jorgan.disposition.Rank.Engaged;
import jorgan.disposition.Rank.NoteMuted;
import jorgan.disposition.Rank.NotePlayed;
import jorgan.disposition.event.OrganEvent;
import jorgan.midi.channel.Channel;
import jorgan.midi.channel.ChannelFilter;
import jorgan.midi.channel.ChannelPool;
import jorgan.midi.channel.DelayedChannel;
import jorgan.midi.mpl.Context;
import jorgan.midi.mpl.ProcessingException;
import jorgan.midi.mpl.Processor;

/**
 * A player of a {@link jorgan.disposition.Rank}.
 */
public class RankPlayer extends Player<Rank> {

	private PlayerContext context = new PlayerContext();

	private ChannelPool channelPool;

	private Channel channel;

	private int[] played = new int[128];

	public RankPlayer(Rank soundSource) {
		super(soundSource);
	}

	@Override
	protected void openImpl() {
		Rank rank = getElement();

		removeProblem(new Error("channels"));
		removeProblem(new Warning("channels"));
		removeProblem(new Error("output"));
		if (rank.getOutput() != null) {
			try {
				// Important: assure successfull opening of MIDI device
				// before storing reference in instance variable
				ChannelPool toBeOpened = ChannelPool.instance(rank.getOutput());
				toBeOpened.open();
				channelPool = toBeOpened;
			} catch (MidiUnavailableException ex) {
				addProblem(new Error("output", rank.getOutput()));
			}
		}
	}

	@Override
	protected void closeImpl() {
		if (channel != null) {
			disengaged();
		}

		if (channelPool != null) {
			channelPool.close();
			channelPool = null;
		}
	}

	private void engaged() {
		Rank rank = getElement();

		for (int n = 0; n < played.length; n++) {
			played[n] = 0;
		}
		
		try {
			channel = channelPool.createChannel(new RankChannelFilter(rank
					.getChannels()));
		} catch (ProcessingException ex) {
			channel = new DeadChannel();

			addProblem(new Error("channels", rank.getChannels()));
			return;
		}

		if (channel == null) {
			channel = new DeadChannel();

			addProblem(new Warning("channels", rank.getChannels()));
			return;
		}

		for (Reference reference : rank.getReferences()) {
			FilterPlayer filterPlayer = (FilterPlayer) getOrganPlay()
					.getPlayer(reference.getElement());

			channel = filterPlayer.filter(channel);
		}

		if (rank.getDelay() > 0) {
			channel = new DelayedChannel(channel, rank.getDelay());
		}

		for (Engaged engaged : getElement().getMessages(Engaged.class)) {
			output(engaged, context);
		}
	}

	private void disengaged() {
		for (Disengaged disengaged : getElement().getMessages(Disengaged.class)) {
			output(disengaged, context);
		}

		channel.release();
		channel = null;
	}

	@Override
	public void output(ShortMessage message, Context context) {
		channel.sendMessage(message);
	}

	@Override
	public void elementChanged(OrganEvent event) {
		super.elementChanged(event);

		Rank rank = getElement();

		if (rank.getOutput() == null && getWarnDevice()) {
			addProblem(new Warning("output"));
		} else {
			removeProblem(new Warning("output"));
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
		if (channelPool != null) {
			if (channel == null) {
				engaged();
			}

			if (played[pitch] == 0) {
				played(pitch, velocity);
			}
			played[pitch]++;
		}
	}

	private void played(int pitch, int velocity) {
		context.set(NotePlayed.PITCH, pitch);
		context.set(NotePlayed.VELOCITY, velocity);
		for (NotePlayed notePlayed : getElement().getMessages(NotePlayed.class)) {
			output(notePlayed, context);
		}
	}

	public void mute(int pitch) {
		if (channelPool != null) {
			if (channel == null) {
				return;
			}
			
			played[pitch]--;
			if (played[pitch] == 0) {
				muted(pitch);
			}
		}
	}

	private void muted(int pitch) {
		context.set(NoteMuted.PITCH, pitch);
		for (NoteMuted noteMuted : getElement().getMessages(NoteMuted.class)) {
			output(noteMuted, context);
		}
	}

	private class DeadChannel implements Channel {
		public void sendMessage(ShortMessage message) {
		}

		public void release() {
		}
	}

	private class RankChannelFilter implements ChannelFilter, Context {

		private Processor processor;

		public RankChannelFilter(String pattern) throws ProcessingException {
			this.processor = new Processor(pattern);
		}

		public boolean accept(int channel) {
			return !Float.isNaN(processor.process(channel, this));
		}

		public float get(String name) {
			throw new UnsupportedOperationException();
		}

		public void set(String name, float value) {
			throw new UnsupportedOperationException();
		}
	}
}