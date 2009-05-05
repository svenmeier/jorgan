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
package jorgan.recorder.midi;

import javax.sound.midi.MidiMessage;

/**
 * A listener of a {@link Recorder}.
 */
public interface RecorderListener {

	/**
	 * Notification of a time change.
	 * 
	 * @param millis
	 * 
	 * @see Recorder#setTime(long)
	 * @see Recorder#first()
	 * @see Recorder#last()
	 */
	public void timeChanged(long millis);
	
	/**
	 * Notification of a change of tracks.
	 * 
	 * @param tracks
	 * 
	 * @see Recorder#setTracks(int)
	 */
	public void tracksChanged(int tracks);
	
	/**
	 * Notification of a recorded message.
	 * 
	 * @param track
	 * @param millis
	 * @param message
	 * 
	 * @see Recorder#record()
	 * @see Recorder#record(int, MidiMessage)
	 */
	public void recorded(int track, long millis, MidiMessage message);

	/**
	 * Notification of a played message.
	 * 
	 * @param track
	 * @param millis
	 * @param message
	 * 
	 * @see Recorder#play()
	 */
	public void played(int track, long millis, MidiMessage message);

	/**
	 * Notification that the recorder is now playing.
	 * 
	 * @see Recorder#play()
	 */
	public void playing();

	/**
	 * Notification that the recorder is now recording.
	 * 
	 * @see Recorder#record()
	 */
	public void recording();
	
	/**
	 * Notification that the recorder is about to be stopped.
	 * 
	 * @see Recorder#stop()
	 */
	public void stopping();

	/**
	 * Notification that the recorder has stopped.
	 * 
	 * @see Recorder#stop()
	 */
	public void stopped();
}