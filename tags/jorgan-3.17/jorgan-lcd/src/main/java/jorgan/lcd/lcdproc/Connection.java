package jorgan.lcd.lcdproc;

import java.io.IOException;

public interface Connection {

	public void send(Parameters parameters) throws IOException;
}