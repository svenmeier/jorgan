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
package jorgan.gui.config;

import java.awt.Component;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JFrame;

import jorgan.config.AbstractConfiguration;
import jorgan.swing.StandardDialog;
import jorgan.util.I18N;

/**
 * A dialog for editing of configurations.
 */
public class ConfigurationDialog extends StandardDialog {

	private static I18N i18n = I18N.get(ConfigurationDialog.class);

	private ConfigurationTreePanel configTreePanel = new ConfigurationTreePanel();

	private AbstractConfiguration configuration;

	/**
	 * Constructor.
	 */
	private ConfigurationDialog(JDialog owner) {
		super(owner);

		init();
	}

	/**
	 * Constructor.
	 */
	private ConfigurationDialog(JFrame owner) {
		super(owner);

		init();
	}

	private void init() {
		setTitle(i18n.getString("title"));

		setBody(configTreePanel);

		addOKAction();
		addCancelAction();
	}

	/**
	 * Set the configuration.
	 * 
	 * @param configuration
	 * @param showRoot
	 */
	public void setConfiguration(AbstractConfiguration configuration,
			boolean showRoot) {
		this.configuration = configuration;

		try {
			configTreePanel.setConfiguration(
					(AbstractConfiguration) configuration.clone(), showRoot);
		} catch (Exception ex) {
			throw new Error("unable to clone configuration '"
					+ configuration.getClass() + "'");
		}
	}

	public void onOK() {
		configTreePanel.write();

		configTreePanel.getConfiguration().backup();

		configuration.restore();

		super.onOK();
	}

	/**
	 * Factory method for an arbitrary owner.
	 * 
	 * @param owner
	 *            the owner of the dialog
	 * @param configuration
	 *            the configuration
	 * @param showRoot
	 *            should root be shown
	 * @return created dialog
	 */
	public static ConfigurationDialog create(Component owner,
			AbstractConfiguration configuration, boolean showRoot) {

		Window window = getWindow(owner);

		ConfigurationDialog dialog;

		if (window instanceof JFrame) {
			dialog = new ConfigurationDialog((JFrame) window);
		} else if (window instanceof JDialog) {
			dialog = new ConfigurationDialog((JDialog) window);
		} else {
			throw new Error("unable to get window ancestor");
		}

		dialog.setConfiguration(configuration, showRoot);

		return dialog;
	}
}