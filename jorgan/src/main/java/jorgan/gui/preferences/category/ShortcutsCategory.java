/*
 * jOrgan - Java Virtual Organ
 * Copyright (C) 2003 Sven Meier
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package jorgan.gui.preferences.category;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jorgan.gui.preferences.spi.ShortcutsRegistry;
import jorgan.swing.KeyField;
import jorgan.swing.layout.DefinitionBuilder;
import jorgan.swing.layout.DefinitionBuilder.Column;
import bias.Configuration;
import bias.swing.Category;

/**
 * {@link Shortcut}s category.
 */
public class ShortcutsCategory extends JOrganCategory {

	private static Configuration config = Configuration.getRoot().get(
			ShortcutsCategory.class);

	public ShortcutsCategory() {
		config.read(this);
	}

	@Override
	public Class<? extends Category> getParentCategory() {
		return GuiCategory.class;
	}

	@Override
	protected JComponent createComponent() {
		JPanel panel = new JPanel();

		DefinitionBuilder builder = new DefinitionBuilder(panel);

		Column column = builder.column();

		for (Shortcut shortcut : ShortcutsRegistry.createShortcuts()) {
			column.term(new JLabel(shortcut.getName()));

			column.definition(new KeyField()).fillHorizontal();
		}

		return panel;
	}

	public static abstract class Shortcut {
		private String name;

		public void setName(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public abstract String getKey();
	}
}