package jorgan.memory.display;

import java.io.IOException;

import jorgan.disposition.Element;
import jorgan.lcd.display.ElementDisplayer;
import jorgan.lcd.display.spi.DisplayerProvider;
import jorgan.lcd.lcdproc.Screen;
import jorgan.memory.Storage;
import jorgan.memory.disposition.Memory;
import jorgan.session.OrganSession;

public class MemoryDisplayerProvider implements DisplayerProvider {

	@Override
	public ElementDisplayer<?> createDisplayer(OrganSession session,
			Element element, Screen screen, int row) throws IOException {
		ElementDisplayer<?> displayer = null;

		if (element instanceof Memory) {
			displayer = new MemoryDisplayer(session.lookup(Storage.class),
					screen, row, (Memory) element);
		}

		return displayer;
	}
}
