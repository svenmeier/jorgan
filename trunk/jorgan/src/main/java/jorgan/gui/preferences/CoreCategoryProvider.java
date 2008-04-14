package jorgan.gui.preferences;

import java.util.ArrayList;
import java.util.List;

import jorgan.gui.preferences.category.AppCategory;
import jorgan.gui.preferences.category.MidiCategory;
import jorgan.gui.preferences.category.DispositionStreamCategory;
import jorgan.gui.preferences.category.GuiCategory;
import jorgan.gui.preferences.category.MidiMergerCategory;
import jorgan.gui.preferences.category.CLICategory;
import jorgan.gui.preferences.category.ConsoleCategory;
import bias.swing.Category;

/**
 * Provider of categories in jOrgan core.
 */
public class CoreCategoryProvider {

	public List<Category> getCategories() {
		List<Category> categories = new ArrayList<Category>();

		categories.add(new AppCategory());
		categories.add(new GuiCategory());
		categories.add(new ConsoleCategory());
		categories.add(new DispositionStreamCategory());
		categories.add(new CLICategory());
		categories.add(new MidiCategory());
		categories.add(new MidiMergerCategory());

		return categories;
	}
}