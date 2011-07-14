package jorgan.lcd.display.spi;

import java.io.IOException;

import jorgan.disposition.Continuous;
import jorgan.disposition.Element;
import jorgan.disposition.Regulator;
import jorgan.disposition.Switch;
import jorgan.lcd.display.ContinuousDisplayer;
import jorgan.lcd.display.ElementDisplayer;
import jorgan.lcd.display.RegulatorDisplayer;
import jorgan.lcd.display.SwitchDisplayer;
import jorgan.lcd.lcdproc.Screen;

public class DisplayerRegistry {

	public ElementDisplayer<?> getDisplayer(Screen screen, int row,
			Element element) throws IOException {
		ElementDisplayer<?> display = null;

		if (element instanceof Switch) {
			display = new SwitchDisplayer(screen, row, (Switch) element);
		} else if (element instanceof Regulator) {
			display = new RegulatorDisplayer(screen, row, (Regulator) element);
		} else if (element instanceof Continuous) {
			display = new ContinuousDisplayer(screen, row, (Continuous) element);
		}

		return display;
	}
}
