package jorgan.executor.gui.preferences;


import java.util.ArrayList;
import java.util.List;

import jorgan.executor.gui.preferences.category.ExecutorCategory;
import jorgan.gui.preferences.spi.CategoryProvider;
import bias.swing.Category;

public class ExecutorCategoryProvider implements CategoryProvider {

	public List<Category> getCategories() {
		List<Category> categories = new ArrayList<Category>();

		categories.add(new ExecutorCategory());

		return categories;
	}
}