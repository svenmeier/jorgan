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
package jorgan.gui.construct;

import java.awt.Component;

import javax.swing.JComponent;

import jorgan.disposition.Element;
import jorgan.disposition.Matcher;
import jorgan.disposition.Organ;
import jorgan.swing.wizard.AbstractPage;
import jorgan.swing.wizard.BasicWizard;
import jorgan.swing.wizard.WizardDialog;
import bias.Configuration;

/**
 * A wizard for creating of a message.
 */
public class CreateMessageWizard extends BasicWizard {

	private static Configuration config = Configuration.getRoot().get(
			CreateMessageWizard.class);

	private Element element;

	private Matcher matcher;

	/**
	 * Create a new wizard.
	 * 
	 * @param organ
	 *            the organ of the element
	 * @param element
	 *            the element to create references for
	 */
	public CreateMessageWizard(Organ organ, Element element) {
		this.element = element;

		addPage(new MessagePage());
	}

	/**
	 * Allows finish only if a message type isselected.
	 * 
	 * @return <code>true</code> if stops are selected
	 */
	@Override
	public boolean allowsFinish() {
		return matcher != null;
	}

	/**
	 * Finish.
	 */
	@Override
	protected boolean finishImpl() {

		element.addMessage(matcher);

		return true;
	}

	/**
	 * Page for selecting a message type.
	 */
	private class MessagePage extends AbstractPage {

		private MessageCreationPanel messagePanel = new MessageCreationPanel();

		private MessagePage() {
			config.get("message").read(this);

			messagePanel.setMessageClasses(element.getMessageClasses());
		}

		@Override
		protected JComponent getComponentImpl() {
			return messagePanel;
		}

		@Override
		protected void changing() {
			matcher = messagePanel.getMatcher();

			super.changing();
		}
	}

	/**
	 * Show an reference creation wizard in a dialog.
	 * 
	 * @param owner
	 *            owner of dialog
	 * @param organ
	 *            the organ of the element
	 * @param element
	 *            element to add created references to
	 */
	public static void showInDialog(Component owner, Organ organ,
			Element element) {

		WizardDialog dialog = WizardDialog.create(owner);
		dialog.setWizard(new CreateMessageWizard(organ, element));

		config.get("dialog").read(dialog);
		dialog.setVisible(true);
		config.get("dialog").write(dialog);

		dialog.dispose();

		dialog.setWizard(null);
	}
}