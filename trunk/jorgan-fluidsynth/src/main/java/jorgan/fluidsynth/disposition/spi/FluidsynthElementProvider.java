package jorgan.fluidsynth.disposition.spi;

import java.util.ArrayList;
import java.util.List;

import jorgan.disposition.Element;
import jorgan.disposition.spi.ElementProvider;
import jorgan.fluidsynth.disposition.FluidsynthSound;

public class FluidsynthElementProvider implements ElementProvider {

	public List<Class<? extends Element>> getElementClasses() {
		List<Class<? extends Element>> classes = new ArrayList<Class<? extends Element>>();

		classes.add(FluidsynthSound.class);

		return classes;
	}
}
