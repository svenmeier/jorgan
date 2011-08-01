package jorgan.lcd.lcdproc;

import java.io.IOException;

public class NumWidget extends Widget {

	private int x;

	public NumWidget(Screen screen, int x) throws IOException {
		super(screen, "num");

		this.x = x;
	}

	public void value(int num) throws IOException {
		set(x, num);
	}
}