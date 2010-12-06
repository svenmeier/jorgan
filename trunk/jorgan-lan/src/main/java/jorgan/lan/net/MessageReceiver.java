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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;

import jorgan.midi.MessageUtils;

public class MessageReceiver {

	private MulticastSocket socket;

	private InetAddress group;

	private int port;

	private Thread thread;

	private byte[] bytes = new byte[256];

	public MessageReceiver(InetAddress group, int port) throws IOException {
		this.port = port;
		this.group = group;

		try {
			socket = new MulticastSocket(this.port);
			socket.joinGroup(this.group);
		} catch (IOException e) {
			close();

			throw e;
		}

		thread = new Thread(new Runnable() {
			public void run() {
				while (thread != null) {
					try {
						receive(socket);
					} catch (IOException ex) {
						if (thread != null) {
							onError(ex);
						}
						break;
					}
				}
			}
		}, "LAN receiver");
		thread.start();
	}

	public void close() {
		if (thread != null) {
			thread = null;

			socket.close();
		}
	}

	private void receive(DatagramSocket socket) throws IOException {
		DatagramPacket packet = new DatagramPacket(bytes, 0, bytes.length);
		socket.receive(packet);

		MidiMessage message;
		try {
			message = MessageUtils.createMessage(bytes, packet.getLength());
		} catch (InvalidMidiDataException ex) {
			onWarning(ex);
			return;
		}

		onReceived(message);
	}

	protected void onError(IOException ex) {
	}

	protected void onWarning(InvalidMidiDataException ex) {
	}

	protected void onReceived(MidiMessage message) {
	}
}