package jorgan.lcd.lcdproc;

import java.io.IOException;

public abstract class Widget extends Component {

	public final Screen screen;

	protected Widget(Screen screen, String string) throws IOException {
		this.screen = screen;

		send(new Parameters("widget_add", screen.id, this.id, string));
	}

	protected void set(Object... values) throws IOException {
		send(new Parameters("widget_set", screen.id, this.id).append(values));
	}

	private void send(Parameters parameters) throws IOException {
		screen.connection.send(parameters);
	}
}