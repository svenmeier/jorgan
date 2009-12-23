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
import java.net.InetAddress;
import java.net.MulticastSocket;

import javax.sound.midi.ShortMessage;

public class MessageSender {

	private MulticastSocket socket;

	private InetAddress group;

	private int port;

	public MessageSender(InetAddress group, int port) throws IOException {
		this.port = port;
		this.group = group;

		try {
			socket = new MulticastSocket();
		} catch (IOException e) {
			close();

			throw e;
		}
	}

	public void close() {
		if (socket != null) {
			socket.close();
			socket = null;
		}
	}

	public void send(ShortMessage message) throws IOException {
		if (socket == null) {
			throw new IllegalStateException("not open");
		}

		DatagramPacket packet = new DatagramPacket(message.getMessage(),
				message.getLength(), group, port);
		socket.send(packet);
	}
}
