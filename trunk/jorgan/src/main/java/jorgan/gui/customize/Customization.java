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
package jorgan.gui.customize;

import java.awt.Component;

import javax.swing.JCheckBox;

import jorgan.session.OrganSession;
import bias.Configuration;
import bias.swing.MessageBox;

/**
 * An offer for customization.
 */
public class Customization {

	private static Configuration config = Configuration.getRoot().get(
			Customization.class);

	private Boolean customizeOnError = null;

	public Customization() {
		config.read(this);
	}

	public void setCustomizeOnError(Boolean customizeOnError) {
		this.customizeOnError = customizeOnError;
	}

	public Boolean getCustomizeOnError() {
		return customizeOnError;
	}

	/**
	 * Offer customization.
	 * 
	 * @param owner
	 *            the owning component
	 * @return <code>true</code> if offer of customizazion is accepted
	 */
	public boolean offer(Component owner) {
		if (customizeOnError == null) {
			MessageBox box = new MessageBox(MessageBox.OPTIONS_YES_NO);
			config.get("confirm").read(box);

			JCheckBox rememberCheckBox = new JCheckBox("");
			config.get("confirm/remember").read(rememberCheckBox);
			box.setComponents(rememberCheckBox);

			boolean customize = (box.show(owner) == MessageBox.OPTION_YES);

			if (rememberCheckBox.isSelected()) {
				customizeOnError = Boolean.valueOf(customize);
			}

			return customize;
		}

		if (customizeOnError.booleanValue()) {
			return true;
		}

		return false;
	}

	/**
	 * Offer customization.
	 * 
	 * @param owner
	 *            owner of dialog
	 * @param session
	 *            organ to configure
	 */
	public static void offer(Component owner, OrganSession session) {

		if (session.getProblems().hasErrors()) {
			Customization customization = new Customization();

			if (customization.offer(owner)) {
				CustomizeWizard.showInDialog(owner, session);
			}

			config.write(customization);
		}
	}
}