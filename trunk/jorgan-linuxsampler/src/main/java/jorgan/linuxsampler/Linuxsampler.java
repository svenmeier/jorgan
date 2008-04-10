/**
 * 
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

public class Linuxsampler {
	
	private static final String COMMENT_PREFIX = "#";

	private Socket socket;

	private Writer writer;

	private Reader reader;

	public Linuxsampler(String host, int port)
			throws UnknownHostException, SocketTimeoutException,
			IOException {

		SocketAddress address = new InetSocketAddress(host, port);

		Socket socket = new Socket();
		socket.connect(address, 1000);

		try {
			writer = new OutputStreamWriter(socket.getOutputStream());
		} catch (IOException e) {
			socket.close();

			throw e;
		}

		try {
			reader = new InputStreamReader(socket.getInputStream());
		} catch (IOException e) {
			socket.close();

			throw e;
		}
	}

	public void send(String lscp) throws IOException {

		BufferedReader reader = new BufferedReader(new StringReader(lscp));

		send(reader);
		
		reader.close();
	}

	public void send(Reader reader) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(reader);
		while (true) {
			String line = bufferedReader.readLine();

			if (line == null) {
				return;
			}

			write(line);
		}
	}

	private void write(String line)  throws IOException {
		line = line.trim();
		
		if (line.length() == 0) {
			return;
		}

		if (line.startsWith(COMMENT_PREFIX)) {
			return;
		}

		writer.write(line);
		writer.write("\r\n");
		writer.flush();
		
		// TODO analyse response
	}
	
	public void close() {
		try {
			writer.close();
			reader.close();
			socket.close();
		} catch (IOException ignore) {
		}
	}
}