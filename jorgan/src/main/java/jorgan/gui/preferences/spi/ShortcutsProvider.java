package jorgan.gui.preferences.spi;

import java.util.List;

import jorgan.gui.preferences.category.ShortcutsCategory.Shortcut;

public interface ShortcutsProvider {

	public List<Shortcut> getShortcuts();
}
