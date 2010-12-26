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
package jorgan.lan.net;

import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;

import jorgan.lan.IpMidi;
import junit.framework.TestCase;

/**
 * Test for {@link MessageReceiver}.
 */
public class MessageReceiverTest extends TestCase {

	private boolean hasWarning;

	private boolean hasError;

	private boolean hasReceived;

	public void test() throws Exception {

		MessageReceiver receiver = new MessageReceiver(IpMidi.GROUP, IpMidi
				.port(0)) {

			@Override
			protected void onReceived(MidiMessage message) {
				hasReceived = true;
			}

			@Override
			protected void onWarning(InvalidMidiDataException ex) {
				hasWarning = true;
			}

			@Override
			protected void onError(IOException ex) {
				hasError = true;
			}
		};

		Thread.sleep(500);

		receiver.close();

		Thread.sleep(500);

		assertFalse(hasReceived);
		assertFalse(hasWarning);
		assertFalse(hasError);
	}
}
