package jorgan.lcd.disposition;

import jorgan.disposition.Displayable;
import jorgan.disposition.Element;
import jorgan.util.Null;

public class Screen extends Element {

	private String host = "localhost";

	private int port = 13666;

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
		return Displayable.class.isAssignableFrom(clazz);
	}
}