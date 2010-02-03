package jorgan.sams.gui.preferences;

import java.util.ArrayList;
import java.util.List;

import jorgan.gui.preferences.spi.CategoryProvider;
import bias.swing.Category;

/**
 * Provider of categories in jOrgan-sams.
 */
public class SamsCategoryProvider implements CategoryProvider {

	public List<Category> getCategories() {
		List<Category> categories = new ArrayList<Category>();

		categories.add(new SamsCategory());

		return categories;
	}
}