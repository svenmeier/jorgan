package jorgan.gui.preferences;

import java.util.ArrayList;
import java.util.List;

import jorgan.gui.preferences.category.ShortcutsCategory;
import jorgan.gui.preferences.category.ShortcutsCategory.Shortcut;
import jorgan.gui.preferences.spi.ShortcutsProvider;
import bias.Configuration;

/**
 * Provider of categories in jOrgan-midimerger.
 */
public class DefaultShortcutsProvider implements ShortcutsProvider {

	private static final Configuration config = Configuration.getRoot().get(
			DefaultShortcutsProvider.class);

	public List<Shortcut> getShortcuts() {
		List<Shortcut> shortcuts = new ArrayList<Shortcut>();

		shortcuts.add(config.get("fullscreen").read(new ShortcutsCategory.Shortcut() {
			@Override
			public String getKey() {
				return "TODO fullscreen";
			}
		}));

		return shortcuts;
	}
}