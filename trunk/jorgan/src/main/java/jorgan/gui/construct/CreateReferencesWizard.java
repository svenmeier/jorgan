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
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import jorgan.disposition.Element;
import jorgan.session.OrganSession;
import jorgan.session.undo.Compound;
import jorgan.swing.wizard.AbstractPage;
import jorgan.swing.wizard.BasicWizard;
import jorgan.swing.wizard.WizardDialog;
import bias.Configuration;

/**
 * A wizard for creating of references.
 */
public class CreateReferencesWizard extends BasicWizard {

	private static Configuration config = Configuration.getRoot().get(
			CreateReferencesWizard.class);

	private OrganSession session;

	private Element element;

	private List<Element> referencesTo = new ArrayList<Element>();

	private List<Element> referencedFrom = new ArrayList<Element>();

	/**
	 * Create a new wizard.
	 * 
	 * @param organ
	 *            the organ of the element
	 * @param element
	 *            the element to create references for
	 */
	public CreateReferencesWizard(OrganSession organ, Element element) {
		this.session = organ;
		this.element = element;

		addPage(new ReferencesToPage());
		addPage(new ReferencedByPage());
	}

	/**
	 * Allows finish only if elements for new references are selected.
	 * 
	 * @return <code>true</code> if stops are selected
	 */
	@Override
	public boolean allowsFinish() {
		return referencesTo.size() > 0 || referencedFrom.size() > 0;
	}

	/**
	 * Finish.
	 */
	@Override
	protected boolean finishImpl() {

		session.getUndoManager().compound(new Compound() {
			public void run() {
				for (int t = 0; t < referencesTo.size(); t++) {
					Element referenced = referencesTo.get(t);
					element.reference(referenced);
				}

				for (int f = 0; f < referencedFrom.size(); f++) {
					Element referrer = referencedFrom.get(f);
					referrer.reference(element);
				}
			}
		});
		
		return true;
	}

	/**
	 * Page for selecting elements to reference to.
	 */
	private class ReferencesToPage extends AbstractPage {

		private ElementsSelectionPanel elementsSelectionPanel = new ElementsSelectionPanel();

		private ReferencesToPage() {
			config.get("referencesTo").read(this);

			elementsSelectionPanel.setElements(session.getOrgan()
					.getReferenceToCandidates(element));
		}

		@Override
		protected JComponent getComponentImpl() {
			return elementsSelectionPanel;
		}

		@Override
		protected void changing() {
			referencesTo = elementsSelectionPanel.getSelectedElements();

			super.changing();
		}
	}

	/**
	 * Page for selecting elements to be referenced from.
	 */
	private class ReferencedByPage extends AbstractPage {

		private ElementsSelectionPanel elementsSelectionPanel = new ElementsSelectionPanel();

		private ReferencedByPage() {
			config.get("referencedBy").read(this);

			elementsSelectionPanel.setElements(session.getOrgan()
					.getReferencedFromCandidates(element));
		}

		@Override
		protected JComponent getComponentImpl() {
			return elementsSelectionPanel;
		}

		@Override
		protected void changing() {
			referencedFrom = elementsSelectionPanel.getSelectedElements();

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
	public static void showInDialog(Component owner, OrganSession organ,
			Element element) {

		WizardDialog dialog = WizardDialog.create(owner);
		dialog.setWizard(new CreateReferencesWizard(organ, element));

		config.get("dialog").read(dialog);
		dialog.setVisible(true);
		config.get("dialog").write(dialog);

		dialog.dispose();

		dialog.setWizard(null);
	}
}