package jorgan.lcd.lcdproc;

import java.io.IOException;

public class IconWidget extends Widget {

	private int x;

	private int y;

	public IconWidget(Screen screen, int x, int y) throws IOException {
		super(screen, "icon");

		this.x = x;
		this.y = y;
	}

	public void value(Icon icon) throws IOException {
		set(x, y, icon.toString());
	}

	public static enum Icon {
		BLOCK_FILLED, HEART_OPEN, HEART_FILLED, ARROW_UP, ARROW_DOWN, ARROW_LEFT, ARROW_RIGHT, CHECKBOX_OFF, CHECKBOX_ON, CHECKBOX_GRAY, SELECTOR_AT_LEFT, SELECTOR_AT_RIGHT, ELLIPSIS, STOP, PAUSE, PLAY, PLAYR, FF, FR, NEXT, PREV, REC
	}
}