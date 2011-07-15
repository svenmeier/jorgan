package jorgan.lcd.display.spi;

import java.io.IOException;

import jorgan.disposition.Element;
import jorgan.lcd.display.ElementDisplayer;
import jorgan.lcd.lcdproc.Screen;
import jorgan.session.OrganSession;

public interface DisplayerProvider {

	public ElementDisplayer<?> createDisplayer(OrganSession session,
			Element element, Screen screen, int row) throws IOException;
}
