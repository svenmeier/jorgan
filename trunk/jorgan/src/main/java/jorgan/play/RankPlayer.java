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

import javax.sound.midi.ShortMessage;

import jorgan.disposition.Element;
import jorgan.disposition.Filter;
import jorgan.disposition.Rank;
import jorgan.disposition.Output;
import jorgan.disposition.Rank.Disengaged;
import jorgan.disposition.Rank.Engaged;
import jorgan.disposition.Rank.NoteMuted;
import jorgan.disposition.Rank.NotePlayed;
import jorgan.disposition.event.OrganEvent;
import jorgan.midi.mpl.Context;
import jorgan.midi.mpl.ProcessingException;
import jorgan.midi.mpl.Processor;
import jorgan.play.output.Channel;
import jorgan.play.output.ChannelFilter;
import jorgan.play.output.DelayedChannel;
import jorgan.session.event.Severity;

/**
 * A player of a {@link jorgan.disposition.Rank}.
 */
public class RankPlayer extends Player<Rank> {

	private PlayerContext context = new PlayerContext();

	private Channel channel;

	private int[] played = new int[128];

	public RankPlayer(Rank soundSource) {
		super(soundSource);
	}

	@Override
	protected void openImpl() {
		removeProblem(Severity.WARNING, "channels");
		removeProblem(Severity.ERROR, "channels");
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

		try {
			for (Output sound : rank.getReferenced(Output.class)) {
				OutputPlayer<?> player = (OutputPlayer<?>) getOrganPlay().getPlayer(
						sound);

				channel = player.createChannel(new RankChannelFilter(rank
						.getChannels()));
				break;
			}
		} catch (ProcessingException ex) {
			channel = new DeadChannel();

			addProblem(Severity.ERROR, "channels", rank.getChannels(),
					"channelsIllegal");
			return;
		}

		if (channel == null) {
			channel = new DeadChannel();

			addProblem(Severity.WARNING, "channels", rank.getChannels(),
					"channelsUnvailable");
			return;
		}

		for (Element element : rank.getReferenced(Element.class)) {
			if (element instanceof Filter) {
				FilterPlayer player = (FilterPlayer) getOrganPlay().getPlayer(
						element);

				channel = player.filter(channel);
			}
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
	public void onOutput(ShortMessage message, Context context) {
		channel.sendMessage(message);
	}

	@Override
	public void elementChanged(OrganEvent event) {
		super.elementChanged(event);

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
		if (isOpen()) {
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