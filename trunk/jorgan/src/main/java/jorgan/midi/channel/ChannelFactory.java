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
package jorgan.midi.channel;

import javax.sound.midi.MidiUnavailableException;

/**
 * A pool of channels.
 */
public interface ChannelFactory {

	public String getDeviceName();

	/**
	 * Open this pool of channels. <br>
	 * Opens the MIDI device on first call.
	 * 
	 * @throws MidiUnavailableException
	 *             if device is not available
	 */
	public void open() throws MidiUnavailableException;

	/**
	 * Create a channel.
	 * 
	 * @return filter filter of channels
	 */
	public Channel createChannel(ChannelFilter filter);

	/**
	 * Close this pool of channels.
	 */
	public void close();
}