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

import jorgan.disposition.ContinuousFilter;
import jorgan.disposition.ContinuousFilter.Engaging;
import jorgan.disposition.Filter.Intercept;
import jorgan.midi.mpl.Context;
import jorgan.play.sound.Channel;
import jorgan.util.Null;

/**
 * A player for a {@link ContinuousFilter}.
 */
public class ContinuousFilterPlayer extends ContinuousPlayer<ContinuousFilter>
		implements FilterPlayer {

	private List<ChannelFilter> channels = new ArrayList<ChannelFilter>();

	public ContinuousFilterPlayer(ContinuousFilter swell) {
		super(swell);
	}

	public Channel filter(Channel channel) {
		return new ChannelFilter(channel);
	}

	@Override
	protected void closeImpl() {
		channels.clear();
	}

	@Override
	public void update() {
		super.update();

		if (isOpen()) {
			for (ChannelFilter channel : channels) {
				channel.update();
			}
		}
	}

	@Override
	public void onOutput(byte[] datas, Context context)
			throws InvalidMidiDataException {
		if (context instanceof ChannelFilter) {
			((ChannelFilter) context).sendFilteredMessage(datas);
		} else {
			super.onOutput(datas, context);
		}
	}

	private class ChannelFilter extends PlayerContext implements Channel {

		private Channel channel;

		private Float value;

		public ChannelFilter(Channel channel) {
			this.channel = channel;

			channels.add(this);
		}

		public void init() {
			update();

			channel.init();
		}

		public void update() {
			float value = getElement().getValue();

			if (!Null.safeEquals(this.value, value)) {
				engaging(value);

				this.value = value;
			}
		}

		public void sendMessage(byte[] datas) throws InvalidMidiDataException {
			ContinuousFilter element = getElement();

			boolean intercepted = false;

			for (Intercept message : element.getMessages(Intercept.class)) {
				if (process(message, datas)) {
					intercepted = true;
				}
			}

			if (intercepted) {
				float value = element.getValue();
				engaging(value);
				this.value = value;
			} else {
				channel.sendMessage(datas);
			}
		}

		private void engaging(float value) {
			for (Engaging engaging : getElement().getMessages(Engaging.class)) {
				set(Engaging.VALUE, value);
				output(engaging, this);
			}
		}

		public void sendFilteredMessage(byte[] datas)
				throws InvalidMidiDataException {
			channel.sendMessage(datas);
		}

		public void release() {
			channel.release();

			channels.remove(this);
		}
	}
}