package jorgan.gui.preferences;

import java.util.ArrayList;
import java.util.List;

import jorgan.gui.preferences.category.AppCategory;
import jorgan.gui.preferences.category.CLICategory;
import jorgan.gui.preferences.category.ConsoleCategory;
import jorgan.gui.preferences.category.GuiCategory;
import jorgan.gui.preferences.category.OpenLoadCategory;
import jorgan.gui.preferences.category.MidiCategory;
import jorgan.gui.preferences.category.MonitorCategory;
import jorgan.gui.preferences.category.SaveCloseCategory;
import jorgan.gui.preferences.spi.CategoryProvider;
import bias.swing.Category;

/**
 * Provider of categories in jOrgan core.
 */
public class DefaultCategoryProvider implements CategoryProvider {

	public List<Category> getCategories() {
		List<Category> categories = new ArrayList<Category>();

		categories.add(new AppCategory());
		categories.add(new GuiCategory());
		categories.add(new OpenLoadCategory());
		categories.add(new SaveCloseCategory());
		categories.add(new MidiCategory());
		categories.add(new ConsoleCategory());
		categories.add(new CLICategory());
		categories.add(new MonitorCategory());

		return categories;
	}
}