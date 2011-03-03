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

	private int id = 0;

	private Socket socket;

	private Writer writer;

	private BufferedReader reader;

	private int width;

	private int height;

	private int cellWidth;

	private int cellHeight;

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
			String response = sendImpl("hello");
			width = parse(response, "wid");
			height = parse(response, "hgt");
			cellWidth = parse(response, "cellwid");
			cellHeight = parse(response, "cellhgt");
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

	private int id() {
		id = id + 1;
		return id;
	}

	private String sendImpl(String command, Object... params)
			throws IOException {
		ensureOpen();

		writer.write(String.format(command, params));
		writer.write("\r\n");
		writer.flush();

		String result = reader.readLine();

		if (result.startsWith("huh?")) {
			throw new IOException(result);
		}

		return result;
	}

	public Screen addScreen() throws IOException {
		return new Screen();
	}

	private class Component {

		protected final int id = id();
	}

	public class Screen extends Component {

		private Screen() throws IOException {
			sendImpl("screen_add %d", this.id);
		}

		public StringWidget addString(int x, int y) throws IOException {
			return new StringWidget(x, y);
		}

		public HBarWidget addHBar(int x, int y, int size) throws IOException {
			return new HBarWidget(x, y, size);
		}

		public VBarWidget addVBar(int x, int y, int size) throws IOException {
			return new VBarWidget(x, y, size);
		}

		public NumWidget addNum(int x) throws IOException {
			return new NumWidget(x);
		}

		public class StringWidget extends Component {

			private int x;

			private int y;

			private StringWidget(int x, int y) throws IOException {
				sendImpl("widget_add %d %d string", Screen.this.id, this.id);

				this.x = x;
				this.y = y;
			}

			public void value(String string) throws IOException {
				sendImpl("widget_set %d %d %d %d %s", Screen.this.id, this.id,
						x, y, string);
			}
		}

		public class NumWidget extends Component {

			private int x;

			private NumWidget(int x) throws IOException {
				sendImpl("widget_add %d %d num", Screen.this.id, this.id);

				this.x = x;
			}

			public void value(int num) throws IOException {
				sendImpl("widget_set %d %d %d %s", Screen.this.id, this.id, x,
						num);
			}
		}

		public class HBarWidget extends Component {

			private int x;

			private int y;

			private int size;

			private HBarWidget(int x, int y, int size) throws IOException {
				sendImpl("widget_add %d %d hbar", Screen.this.id, this.id);

				this.x = x;
				this.y = y;
				this.size = size;
			}

			public void value(double length) throws IOException {
				int pixels = size * cellWidth;

				sendImpl("widget_set %d %d %d %d %d", Screen.this.id, this.id,
						x, y, Math.round(pixels * length));
			}
		}

		public class VBarWidget extends Component {

			private int x;

			private int y;

			private int size;

			private VBarWidget(int x, int y, int size) throws IOException {

				sendImpl("widget_add %d %d vbar", Screen.this.id, this.id);

				this.x = x;
				this.y = y;
				this.size = size;
			}

			public void value(double length) throws IOException {
				int pixels = size * cellHeight;

				sendImpl("widget_set %d %d %d %d %d", Screen.this.id, this.id,
						x, y, Math.round(pixels * length));
			}
		}
	}
}