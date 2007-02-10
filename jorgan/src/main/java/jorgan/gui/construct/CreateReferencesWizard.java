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
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import jorgan.disposition.Element;
import jorgan.disposition.Organ;
import jorgan.swing.wizard.AbstractPage;
import jorgan.swing.wizard.BasicWizard;
import jorgan.swing.wizard.WizardDialog;
import jorgan.util.I18N;

/**
 * A wizard for creating of references.
 */
public class CreateReferencesWizard extends BasicWizard {

	private static I18N i18n = I18N.get(CreateReferencesWizard.class);

	private Organ organ;

	private Element element;

	private List referencesTo = new ArrayList();

	private List referencedFrom = new ArrayList();

	/**
	 * Create a new wizard.
	 * 
	 * @param organ
	 *            the organ of the element
	 * @param element
	 *            the element to create references for
	 */
	public CreateReferencesWizard(Organ organ, Element element) {
		this.organ = organ;
		this.element = element;

		addPage(new ReferencesToPage());
		addPage(new ReferencedByPage());
	}

	/**
	 * Allows finish only if elements for new references are selected.
	 * 
	 * @return <code>true</code> if stops are selected
	 */
	public boolean allowsFinish() {
		return referencesTo.size() > 0 || referencedFrom.size() > 0;
	}

	/**
	 * Finish.
	 */
	protected boolean finishImpl() {

		for (int t = 0; t < referencesTo.size(); t++) {
			Element referenced = (Element) referencesTo.get(t);
			element.reference(referenced);
		}

		for (int f = 0; f < referencedFrom.size(); f++) {
			Element referrer = (Element) referencedFrom.get(f);
			referrer.reference(element);
		}

		return true;
	}

	/**
	 * Page for selecting elements to reference to.
	 */
	private class ReferencesToPage extends AbstractPage {

		private ElementsSelectionPanel elementsSelectionPanel = new ElementsSelectionPanel();

		private ReferencesToPage() {

			elementsSelectionPanel.addPropertyChangeListener(this);

			elementsSelectionPanel.setElements(organ
					.getReferenceToCandidates(element));
		}

		public String getDescription() {
			return i18n.getString("referencesToPage.description");
		}

		public JComponent getComponent() {
			return elementsSelectionPanel;
		}

		public void propertyChange(PropertyChangeEvent evt) {
			referencesTo = elementsSelectionPanel.getSelectedElements();

			super.propertyChange(evt);
		}
	}

	/**
	 * Page for selecting elements to be referenced from.
	 */
	private class ReferencedByPage extends AbstractPage {

		private ElementsSelectionPanel elementsSelectionPanel = new ElementsSelectionPanel();

		private ReferencedByPage() {

			elementsSelectionPanel.addPropertyChangeListener(this);

			elementsSelectionPanel.setElements(organ
					.getReferencedFromCandidates(element));
		}

		public String getDescription() {
			return i18n.getString("referencedFromPage.description");
		}

		public JComponent getComponent() {
			return elementsSelectionPanel;
		}

		public void propertyChange(PropertyChangeEvent evt) {
			referencedFrom = elementsSelectionPanel.getSelectedElements();

			super.propertyChange(evt);
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

		dialog.setTitle(i18n.getString("title"));

		dialog.setWizard(new CreateReferencesWizard(organ, element));

		dialog.start();

		dialog.dispose();

		dialog.setWizard(null);
	}
}