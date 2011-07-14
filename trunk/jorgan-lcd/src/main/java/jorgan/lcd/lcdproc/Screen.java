package jorgan.lcd.lcdproc;

import java.io.IOException;

public class Screen extends Component {

	public final Dimension size;
	public final Dimension resolution;
	public final Connection connection;

	public Screen(Dimension size, Dimension resolution, Connection connection)
			throws IOException {
		this.size = size;
		this.resolution = resolution;
		this.connection = connection;

		connection.send(new Parameters("screen_add", this.id));
	}
}
