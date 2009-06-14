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
package jorgan.midimerger;

public class MergeInput {

	private String device;

	private int channel;

	/**
	 * @param channelColonDevice	channel and device separated by a colon
	 * @see #toString()
	 */
	public MergeInput(String channelColonDevice) {

		int colon = channelColonDevice.indexOf(':');
		if (colon == -1) {
			throw new IllegalArgumentException("channel and device expected in'" + channelColonDevice);
		}
		
		this.channel = Integer.parseInt(channelColonDevice.substring(0, colon));
		this.device = channelColonDevice.substring(colon + 1);
	}

	public MergeInput(String device, int channel) {
		this.device = device;
		this.channel = channel;
	}

	public String getDevice() {
		return device;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	@Override
	public String toString() {
		return channel + ":" + device;
	}
}