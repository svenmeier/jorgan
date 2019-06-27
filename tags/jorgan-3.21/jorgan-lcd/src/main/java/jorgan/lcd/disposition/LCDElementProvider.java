package jorgan.lcd.disposition;

import java.util.ArrayList;
import java.util.List;

import jorgan.disposition.Element;
import jorgan.disposition.Organ;
import jorgan.disposition.spi.ElementProvider;

public class LCDElementProvider implements ElementProvider {

	@Override
	public void init(Organ organ) {
	}

	@Override
	public List<Class<? extends Element>> getElementClasses(Organ organ) {
		List<Class<? extends Element>> classes = new ArrayList<Class<? extends Element>>();

		classes.add(Display.class);

		return classes;
	}
}
