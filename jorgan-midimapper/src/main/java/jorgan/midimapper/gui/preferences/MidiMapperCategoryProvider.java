package jorgan.midimapper.gui.preferences;

import java.util.ArrayList;
import java.util.List;

import jorgan.gui.preferences.spi.CategoryProvider;
import bias.swing.Category;

/**
 * Provider of categories in jOrgan-midimerger.
 */
public class MidiMapperCategoryProvider implements CategoryProvider {

	@Override
	public List<Category> getCategories() {
		List<Category> categories = new ArrayList<Category>();

		categories.add(new MidiMapperCategory());

		return categories;
	}
}
