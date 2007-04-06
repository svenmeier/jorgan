package jorgan.gui.preferences;

import java.util.ArrayList;
import java.util.List;

import jorgan.gui.preferences.category.AppCategory;
import jorgan.gui.preferences.category.ChanneledSoundFactoryCategory;
import jorgan.gui.preferences.category.ConsolePanelCategory;
import jorgan.gui.preferences.category.DispositionStreamCategory;
import jorgan.gui.preferences.category.GUICategory;
import jorgan.gui.preferences.category.MidiMergerCategory;
import jorgan.gui.preferences.category.MidiMonitorCategory;
import jorgan.gui.preferences.category.OrganFrameCategory;
import jorgan.gui.preferences.category.OrganShellCategory;
import jorgan.gui.preferences.category.PlayerCategory;
import jorgan.gui.preferences.category.ViewCategory;
import bias.swing.Category;

/**
 * Provider of categories in jOrgan core.
 */
public class CoreCategoryProvider {

	public List<Category> getCategories() {
		List<Category> categories = new ArrayList<Category>();

		categories.add(new AppCategory());
		categories.add(new GUICategory());
		categories.add(new OrganFrameCategory());
		categories.add(new ConsolePanelCategory());
		categories.add(new ViewCategory());
		categories.add(new MidiMonitorCategory());
		categories.add(new DispositionStreamCategory());
		categories.add(new OrganShellCategory());
		categories.add(new PlayerCategory());
		categories.add(new ChanneledSoundFactoryCategory());
		categories.add(new MidiMergerCategory());

		return categories;
	}
}