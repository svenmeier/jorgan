package jorgan.lcd.display;

import java.io.IOException;

import jorgan.disposition.Continuous;
import jorgan.disposition.Element;
import jorgan.disposition.Label;
import jorgan.disposition.Regulator;
import jorgan.disposition.Switch;
import jorgan.lcd.display.spi.DisplayerProvider;
import jorgan.lcd.lcdproc.Screen;
import jorgan.session.OrganSession;

public class DefaultDisplayerProvider implements DisplayerProvider {

	@Override
	public ElementDisplayer<?> createDisplayer(OrganSession session,
			Element element, Screen screen, int row) throws IOException {
		ElementDisplayer<?> displayer = null;

		if (element instanceof Label) {
			displayer = new LabelDisplayer(screen, row, (Label) element);
		} else if (element instanceof Switch) {
			displayer = new SwitchDisplayer(screen, row, (Switch) element);
		} else if (element instanceof Regulator) {
			displayer = new RegulatorDisplayer(screen, row, (Regulator) element);
		} else if (element instanceof Continuous) {
			displayer = new ContinuousDisplayer(screen, row,
					(Continuous) element);
		}

		return displayer;
	}
}
