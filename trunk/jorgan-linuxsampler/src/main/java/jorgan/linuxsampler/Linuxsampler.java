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
	
	private static final String RESET = "RESET";

	private static final String COMMENT_PREFIX = "#";

	private Socket socket;

	private Writer writer;

	private BufferedReader reader;

	public Linuxsampler(String host, int port)
			throws UnknownHostException, SocketTimeoutException,
			IOException {

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
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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

	public void sendReset() throws IOException {
		sendImpl(RESET);
	}
	
	public void send(Reader reader) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(reader);
		while (true) {
			String line = bufferedReader.readLine();

			if (line == null) {
				return;
			}

			sendImpl(line);
		}
	}

	private void sendImpl(String line) throws IOException {
		String request = line.trim();
		
		if (request.length() == 0) {
			return;
		}

		if (request.startsWith(COMMENT_PREFIX)) {
			return;
		}

		String response = initiate(request);
		// TODO analyse response
	}
	
	private String initiate(String request) throws IOException {

		writer.write(request);
		writer.write("\r\n");
		writer.flush();

		return reader.readLine();
	}
	
	public void close() throws IOException {
		Socket tempSocket = socket;
		
		writer = null;
		reader = null;
		socket = null;
		
		tempSocket.close();
	}
}