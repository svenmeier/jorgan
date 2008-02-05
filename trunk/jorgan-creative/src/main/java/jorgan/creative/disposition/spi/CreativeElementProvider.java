package jorgan.creative.disposition.spi;

import java.util.ArrayList;
import java.util.List;

import jorgan.creative.disposition.CreativeOutput;
import jorgan.disposition.Element;
import jorgan.disposition.spi.ElementProvider;

public class CreativeElementProvider implements ElementProvider {

	public List<Class<? extends Element>> getElementClasses() {
		List<Class<? extends Element>> classes = new ArrayList<Class<? extends Element>>();

		classes.add(CreativeOutput.class);

		return classes;
	}
}
