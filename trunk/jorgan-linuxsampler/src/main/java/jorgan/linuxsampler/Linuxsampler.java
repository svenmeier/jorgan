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
package jorgan.linuxsampler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import jorgan.util.IOUtils;

public class Linuxsampler {

	private Socket socket;

	private Writer writer;

	private BufferedReader reader;

	public Linuxsampler(String host, int port) throws UnknownHostException,
			SocketTimeoutException, IOException {

		SocketAddress address = new InetSocketAddress(host, port);

		socket = new Socket();
		socket.connect(address, 1000);
		socket.setSoTimeout(10 * 1000);

		try {
			writer = new OutputStreamWriter(socket.getOutputStream());
		} catch (IOException e) {
			socket.close();

			throw e;
		}

		try {
			reader = new BufferedReader(new InputStreamReader(socket
					.getInputStream()));
		} catch (IOException e) {
			socket.close();

			throw e;
		}
	}

	public Conversation conversation() {
		ensureOpen();

		return new Conversation();
	}

	public void close() throws IOException {
		ensureOpen();

		Socket tempSocket = socket;

		writer = null;
		reader = null;
		socket = null;

		tempSocket.close();
	}

	private void ensureOpen() {
		if (socket == null) {
			throw new IllegalStateException("already closed");
		}
	}

	public class Conversation {
		private int warnings;

		private Conversation() {
		}

		public void send(String lscp) throws IOException, ConversationException {

			BufferedReader reader = new BufferedReader(new StringReader(lscp));

			try {
				send(reader);
			} finally {
				IOUtils.closeQuietly(reader);
			}
		}

		public void send(Reader reader) throws IOException,
				ConversationException {
			BufferedReader bufferedReader = new BufferedReader(reader);
			while (true) {
				String command = bufferedReader.readLine();

				if (command == null) {
					return;
				}

				sendImpl(command);
			}
		}

		private void sendImpl(String command) throws IOException,
				ConversationException {
			ensureOpen();

			command = command.trim();

			// query commands have a undefined result length so replace with
			// an empty command
			if (command.startsWith("GET") || command.startsWith("LIST")) {
				command = "";
			}

			// echoing our commands will interfere with our result reading
			if (command.startsWith("SET ECHO 1")) {
				command = "";
			}

			writer.write(command);
			writer.write("\r\n");
			writer.flush();

			// empty commands and comments don't have a result
			if (command.length() == 0 || command.startsWith("#")) {
				return;
			}

			String result = reader.readLine();

			if (result.startsWith("ERR")) {
				throw new ConversationException(result);
			}

			if (result.startsWith("WRN")) {
				warnings++;
			}
		}

		public int getWarnings() {
			return warnings;
		}

		public boolean hasWarnings() {
			return warnings > 0;
		}
	}
}