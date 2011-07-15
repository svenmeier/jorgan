package jorgan.lcd.display.spi;

import java.io.IOException;

import jorgan.disposition.Element;
import jorgan.lcd.display.ElementDisplayer;
import jorgan.lcd.lcdproc.Screen;
import jorgan.session.OrganSession;
import jorgan.util.PluginUtils;

public class DisplayerRegistry {

	public static ElementDisplayer<?> getDisplayer(OrganSession session,
			Element element, Screen screen, int row) throws IOException {
		ElementDisplayer<?> displayer = null;

		for (DisplayerProvider provider : PluginUtils
				.lookup(DisplayerProvider.class)) {
			ElementDisplayer<?> candidate = provider.createDisplayer(session,
					element, screen, row);
			if (candidate != null) {
				// prefer more specific displayer
				if (displayer == null
						|| displayer.getClass().isAssignableFrom(
								candidate.getClass())) {
					displayer = candidate;
				}
			}
		}

		return displayer;
	}
}
