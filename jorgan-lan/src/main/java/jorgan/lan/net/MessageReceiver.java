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
import javax.sound.midi.ShortMessage;

public class MessageReceiver {

	private MulticastSocket socket;

	private InetAddress group;

	private int port;

	private Thread thread;

	private byte[] bytes = new byte[3];

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
			@Override
			public void run() {
				DatagramSocket socket = MessageReceiver.this.socket;
				while (socket != null) {
					try {
						receive(socket);
					} catch (IOException ex) {
						if (MessageReceiver.this.socket != null) {
							onError(ex);
						}
						break;
					}
				}
			}
		});
		thread.start();
	}

	public void close() {
		if (socket != null) {
			DatagramSocket socket = this.socket;
			this.socket = null;
			socket.close();
		}
	}

	private void receive(DatagramSocket socket) throws IOException {
		bytes[0] = 0;
		bytes[1] = 0;
		bytes[2] = 0;
		DatagramPacket packet = new DatagramPacket(bytes, 0, 3);
		socket.receive(packet);

		ShortMessage message = new ShortMessage();
		try {
			message.setMessage(bytes[0], bytes[1], bytes[2]);
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

	protected void onReceived(ShortMessage message) {
	}
}