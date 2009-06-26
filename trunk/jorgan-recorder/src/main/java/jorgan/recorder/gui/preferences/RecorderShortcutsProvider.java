package jorgan.recorder.gui.preferences;

import java.util.ArrayList;
import java.util.List;

import jorgan.gui.preferences.category.ShortcutsCategory;
import jorgan.gui.preferences.category.ShortcutsCategory.Shortcut;
import jorgan.gui.preferences.spi.ShortcutsProvider;
import bias.Configuration;

/**
 * Provider of categories in jOrgan-midimerger.
 */
public class RecorderShortcutsProvider implements ShortcutsProvider {

	private static final Configuration config = Configuration.getRoot().get(
			RecorderShortcutsProvider.class);

	public List<Shortcut> getShortcuts() {
		List<Shortcut> shortcuts = new ArrayList<Shortcut>();

		shortcuts.add(config.get("stop").read(new ShortcutsCategory.Shortcut() {
			@Override
			public String getKey() {
				return "TODO stop";
			}
		}));

		shortcuts.add(config.get("play").read(new ShortcutsCategory.Shortcut() {
			@Override
			public String getKey() {
				return "TODO play";
			}
		}));

		shortcuts.add(config.get("record").read(
				new ShortcutsCategory.Shortcut() {
					@Override
					public String getKey() {
						return "TODO record";
					}
				}));

		return shortcuts;
	}
}