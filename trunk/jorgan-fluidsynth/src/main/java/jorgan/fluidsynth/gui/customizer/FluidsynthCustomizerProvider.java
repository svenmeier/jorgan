package jorgan.fluidsynth.gui.customizer;

import java.util.ArrayList;
import java.util.List;

import jorgan.customizer.gui.Customizer;
import jorgan.customizer.gui.spi.CustomizerProvider;
import jorgan.session.OrganSession;

public class FluidsynthCustomizerProvider implements CustomizerProvider {

	public List<Customizer> getCustomizers(OrganSession session) {
		List<Customizer> customizers = new ArrayList<Customizer>();

		if (FluidsynthSoundsCustomizer.customizes(session)) {
			customizers.add(new FluidsynthSoundsCustomizer(session));
		}

		return customizers;
	}
}
