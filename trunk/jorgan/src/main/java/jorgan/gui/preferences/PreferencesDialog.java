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
package jorgan.gui.preferences;

import java.awt.Component;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JFrame;

import jorgan.gui.preferences.spi.ProviderRegistry;
import jorgan.swing.StandardDialog;
import bias.Configuration;
import bias.swing.CategoriesPanel;
import bias.swing.Category;

/**
 * A dialog for editing of configurations.
 */
public class PreferencesDialog extends StandardDialog {

	private static Configuration config = Configuration.getRoot().get(
			PreferencesDialog.class);

	private CategoriesPanel categoriesPanel = new CategoriesPanel() {
		@Override
		protected void categorySelected(Category category) {
			super.categorySelected(category);
			
			PreferencesDialog.this.guaranteeSize();
		}
	};

	/**
	 * Constructor.
	 */
	private PreferencesDialog(JDialog owner) {
		super(owner);

		init();
	}

	/**
	 * Constructor.
	 */
	private PreferencesDialog(JFrame owner) {
		super(owner);

		init();
	}

	private void init() {
		categoriesPanel.setCategories(ProviderRegistry.createCategories());
		setBody(categoriesPanel);

		addOKAction();
		addCancelAction();
	}

	@Override
	public void onOK() {
		categoriesPanel.applyAll();

		super.onOK();
	}

	/**
	 * Show a preference dialog with the given owner.
	 * 
	 * @param owner
	 *            the owner of the dialog
	 */
	public static void show(Component owner) {

		Window window = getWindow(owner);

		PreferencesDialog dialog;

		if (window instanceof JFrame) {
			dialog = new PreferencesDialog((JFrame) window);
		} else if (window instanceof JDialog) {
			dialog = new PreferencesDialog((JDialog) window);
		} else {
			throw new Error("unable to get window ancestor");
		}

		config.read(dialog);
		dialog.setVisible(true);
		config.write(dialog);
		dialog.dispose();
	}
}