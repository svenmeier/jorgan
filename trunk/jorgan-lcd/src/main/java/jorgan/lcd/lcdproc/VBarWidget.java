package jorgan.lcd.lcdproc;

import java.io.IOException;

public class VBarWidget extends Widget {

	private int x;

	private int y;

	private int size;

	public VBarWidget(Screen screen, int x, int y, int size) throws IOException {
		super(screen, "vbar");

		this.x = x;
		this.y = y;
		this.size = size;
	}

	public void value(double length) throws IOException {
		int pixels = size * screen.resolution.height;

		set(x, y, Math.round(pixels * length));
	}
}
