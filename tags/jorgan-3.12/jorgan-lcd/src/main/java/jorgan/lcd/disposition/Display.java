package jorgan.lcd.disposition;

import jorgan.disposition.Element;
import jorgan.util.Null;

public class Display extends Element {

	public static final int DEFAULT_PORT = 13666;

	private String host;

	private int port = DEFAULT_PORT;

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public void setHost(String host) {
		if (!Null.safeEquals(this.host, host)) {
			String oldHost = this.host;

			if (host == null) {
				host = "";
			}
			this.host = host.trim();

			fireChange(new PropertyChange(oldHost, this.host));
		}
	}

	public void setPort(int port) {
		if (this.port != port) {
			int oldPort = this.port;

			this.port = port;

			fireChange(new PropertyChange(oldPort, this.port));
		}
	}

	@Override
	protected boolean canReference(Class<? extends Element> clazz) {
		return Screen.class == clazz;
	}
}