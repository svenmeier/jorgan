package jorgan.recorder.gui.preferences;

import java.util.ArrayList;
import java.util.List;

import jorgan.gui.preferences.spi.CategoryProvider;

import bias.swing.Category;

/**
 * Provider of categories in jOrgan-midimerger.
 */
public class RecorderCategoryProvider implements CategoryProvider {

	public List<Category> getCategories() {
		List<Category> categories = new ArrayList<Category>();

		categories.add(new RecorderCategory());

		return categories;
	}
}