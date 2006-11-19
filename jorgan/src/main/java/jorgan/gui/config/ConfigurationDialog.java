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
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import jorgan.config.AbstractConfiguration;
import jorgan.swing.StandardDialog;

/**
 * A dialog for editing of configurations.
 */
public class ConfigurationDialog extends StandardDialog {

	/**
	 * The resource bundle.
	 */
	protected static ResourceBundle resources = ResourceBundle
			.getBundle("jorgan.gui.resources");

	private ConfigurationTreePanel configTreePanel = new ConfigurationTreePanel();

	private AbstractConfiguration configuration;

	/**
	 * Constructor.
	 */
	private ConfigurationDialog(JFrame owner,
			AbstractConfiguration configuration, boolean showRoot) {
		super(owner);

		setTitle(resources.getString("config.title"));

		setContent(configTreePanel);

		addOKAction();
		addCancelAction();

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
		Window window;
		if (owner instanceof JFrame) {
			window = (JFrame) owner;
		} else {
			window = SwingUtilities.getWindowAncestor(owner);
		}

		return new ConfigurationDialog((JFrame) window, configuration, showRoot);
	}
}