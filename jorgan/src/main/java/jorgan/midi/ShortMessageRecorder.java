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
package jorgan.midi;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

/**
 * Recorder of {@link javax.sound.midi.ShortMessage}s.
 */
public abstract class ShortMessageRecorder {

	private MidiDevice device;

	private Transmitter transmitter;

	/**
	 * Create a recorder for a short message of a device.
	 * 
	 * @param deviceName
	 *            name of device to record from
	 * @throws MidiUnavailableException
	 *             if device is unavailable
	 */
	public ShortMessageRecorder(String deviceName)
			throws MidiUnavailableException {

		device = DevicePool.instance().getMidiDevice(deviceName, Direction.IN);

		device.open();

		transmitter = device.getTransmitter();
		transmitter.setReceiver(new Receiver() {
			private boolean keepRecording = true;

			public void send(MidiMessage message, long when) {
				if (keepRecording) {
					keepRecording = messageRecorded(message);
				}
			}

			public void close() {
			}
		});
	}

	/**
	 * Close recording.
	 */
	public void close() {
		transmitter.close();
		device.close();
	}

	/**
	 * Notification that a message was recorded.
	 * 
	 * @param message
	 *            recorded message
	 * @return should recording be kept
	 */
	public abstract boolean messageRecorded(MidiMessage message);
}
