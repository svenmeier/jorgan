package jorgan.lcd.lcdproc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * A client of {@link http://lcdproc.sourceforge.net}.
 */
public class Client {

	private static final String DEFAULT_HOST = "localhost";

	private static final int DEFAULT_PORT = 13666;

	private Socket socket;

	private Writer writer;

	private BufferedReader reader;

	private int width;

	private int height;

	private Resolution resolution;

	public Client() throws SocketTimeoutException, UnknownHostException,
			IOException {
		this(DEFAULT_HOST, DEFAULT_PORT);
	}

	/**
	 * Create a client to <code>LCDd</code>.
	 */
	public Client(String host, int port) throws UnknownHostException,
			SocketTimeoutException, IOException {

		SocketAddress address = new InetSocketAddress(host, port);

		socket = new Socket();
		socket.connect(address, 1000);
		socket.setSoTimeout(10 * 1000);

		try {
			writer = new OutputStreamWriter(socket.getOutputStream());

			reader = new BufferedReader(new InputStreamReader(socket
					.getInputStream()));

			// response = "connect LCDproc 0.5dev protocol 0.3 lcd wid 20 hgt 4
			// cellwid 5 cellhgt 8"
			String response = sendImpl(new Parameters("hello"));
			width = parse(response, "wid");
			height = parse(response, "hgt");
			resolution = new Resolution(parse(response, "cellwid"), parse(
					response, "cellhgt"));
		} catch (IOException e) {
			socket.close();

			throw e;
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void close() throws IOException {
		ensureOpen();

		Socket tempSocket = socket;

		writer = null;
		reader = null;
		socket = null;

		tempSocket.close();
	}

	private int parse(String response, String key) throws IOException {
		try {
			int start = response.indexOf(key) + key.length() + 1;
			int end = response.indexOf(" ", start);
			if (end == -1) {
				end = response.length();
			}
			return Integer.parseInt(response.substring(start, end));
		} catch (Exception e) {
			throw new IOException("invalid key '" + key + "'");
		}
	}

	private void ensureOpen() {
		if (socket == null) {
			throw new IllegalStateException("already closed");
		}
	}

	private String sendImpl(Parameters parameters) throws IOException {
		ensureOpen();

		writer.write(parameters.toString());
		writer.write("\r\n");
		writer.flush();

		String result = reader.readLine();

		if (result.startsWith("huh?")) {
			throw new IOException(result);
		}

		return result;
	}

	public Screen addScreen() throws IOException {
		return new Screen(resolution, new ConnectionImpl());
	}

	private class ConnectionImpl implements Connection {
		@Override
		public void send(Parameters parameters) throws IOException {
			sendImpl(parameters);
		}
	}
}