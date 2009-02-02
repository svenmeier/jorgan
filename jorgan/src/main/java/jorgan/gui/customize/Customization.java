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

	private boolean prompt = false;

	private OrganSession session;

	public Customization(OrganSession session) {
		config.read(this);

		this.session = session;
	}

	public void setPrompt(boolean prompt) {
		this.prompt = prompt;
	}

	public boolean getPrompt() {
		return prompt;
	}

	public void offer(Component owner) {
		if (prompt) {
			MessageBox box = new MessageBox(MessageBox.OPTIONS_YES_NO);
			config.get("confirm").read(box);

			JCheckBox checkBox = new JCheckBox("", prompt);
			config.get("confirm/alwaysPrompt").read(checkBox);
			box.setComponents(checkBox);

			if (box.show(owner) == MessageBox.OPTION_YES) {
				CustomizeWizard.showInDialog(owner, session);
			}

			this.prompt = checkBox.isSelected();
		}

		config.write(this);
	}

	/**
	 * Show customize in a dialog.
	 * 
	 * @param owner
	 *            owner of dialog
	 * @param session
	 *            organ to configure
	 */
	public static void offer(Component owner, OrganSession session) {

		if (true) { // session.getProblems().hasErrors()
			Customization customization = new Customization(session);

			customization.offer(owner);
		}
	}
}