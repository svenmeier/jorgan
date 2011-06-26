package jorgan.lcd.lcdproc;

import java.io.IOException;

public class StringWidget extends Widget {

	private static final String QUOTE = "\"";

	private static final String ESCAPED_QUOTE = "\\\"";

	private int x;

	private int y;

	public StringWidget(Screen screen, int x, int y) throws IOException {
		super(screen, "string");

		this.x = x;
		this.y = y;
	}

	public void value(String string) throws IOException {
		set(x, y, QUOTE + string.replace(QUOTE, ESCAPED_QUOTE) + QUOTE);
	}
}