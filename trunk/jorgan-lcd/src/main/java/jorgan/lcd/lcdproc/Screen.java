package jorgan.lcd.lcdproc;

import java.io.IOException;

public class Screen extends Component {

	public final Resolution resolution;
	public final Connection connection;

	public Screen(Resolution resolution, Connection connection)
			throws IOException {
		this.resolution = resolution;
		this.connection = connection;

		connection.send(new Parameters("screen_add", this.id));
	}
}
