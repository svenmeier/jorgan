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
import jorgan.disposition.Organ;
import jorgan.disposition.Reference;
import jorgan.swing.wizard.AbstractPage;
import jorgan.swing.wizard.BasicWizard;
import jorgan.swing.wizard.WizardDialog;
import bias.Configuration;

/**
 * A wizard for creating of elements.
 */
public class CreateElementWizard extends BasicWizard {

	private static Configuration config = Configuration.getRoot().get(
			CreateElementWizard.class);

	private Organ organ;

	private Element prototype;

	private Element element;

	private List<Element> referencesTo = new ArrayList<Element>();

	private List<Element> referencedFrom = new ArrayList<Element>();

	/**
	 * Create a new wizard.
	 * 
	 * @param organ
	 *            the organ to creat element for
	 * @param prototype
	 *            an optional prototype for the new element
	 */
	public CreateElementWizard(Organ organ, Element prototype) {
		this.organ = organ;
		this.prototype = prototype;

		addPage(new ElementPage());
		addPage(new ReferencesToPage());
		addPage(new ReferencedByPage());
	}

	/**
	 * Allows finish only if element is created.
	 * 
	 * @return <code>true</code> if stops are selected
	 */
	public boolean allowsFinish() {
		return element != null;
	}

	/**
	 * Finish.
	 */
	protected boolean finishImpl() {

		organ.addElement(element);

		if (!referencesTo.isEmpty()) {
			for (int r = 0; r < referencesTo.size(); r++) {
				element.reference(referencesTo.get(r));
			}
		}

		if (!referencedFrom.isEmpty()) {
			for (int r = 0; r < referencedFrom.size(); r++) {
				referencedFrom.get(r).reference(element);
			}
		}

		return true;
	}

	/**
	 * Page for entering element properties.
	 */
	private class ElementPage extends AbstractPage {

		private ElementCreationPanel elementPanel = new ElementCreationPanel();

		/**
		 * Constructor.
		 */
		public ElementPage() {
			config.get("elementPage").read(this);

			elementPanel.setElementClasses(Organ.getElementClasses());
			if (prototype != null) {
				elementPanel.setElementClass(prototype.getClass());
			}
		}

		protected JComponent getComponentImpl() {
			return elementPanel;
		}

		public boolean allowsNext() {
			return element != null;
		}

		protected void changing() {
			Class elementClass = elementPanel.getElementClass();
			String elementName = elementPanel.getElementName();

			if (elementClass != null && elementName != null) {
				try {
					if (prototype != null
							&& prototype.getClass() == elementClass) {
						element = (Element) prototype.clone();
					} else {
						element = (Element) elementClass.newInstance();
					}

					element.setName(elementName);
				} catch (Exception ex) {
					throw new Error(ex);
				}
			}

			super.changing();
		}
	}

	/**
	 * Page for selecting elements to reference to.
	 */
	private class ReferencesToPage extends AbstractPage {

		private ElementsSelectionPanel elementsSelectionPanel = new ElementsSelectionPanel();

		public ReferencesToPage() {
			config.get("referencesToPage").read(this);
		}

		protected JComponent getComponentImpl() {
			return elementsSelectionPanel;
		}

		public void enteringFromPrevious() {
			elementsSelectionPanel.setElements(organ
					.getReferenceToCandidates(element));

			referencesTo = new ArrayList<Element>();
			if (prototype != null) {
				if (prototype.getClass() == element.getClass()) {
					for (int r = 0; r < prototype.getReferenceCount(); r++) {
						Reference reference = prototype.getReference(r);
						referencesTo.add(reference.getElement());
					}
					elementsSelectionPanel.setSelectedElements(referencesTo);
				}
			}
		}

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

		public ReferencedByPage() {
			config.get("referencedFromPage").read(this);
		}

		protected JComponent getComponentImpl() {
			return elementsSelectionPanel;
		}

		public void enteringFromPrevious() {
			elementsSelectionPanel.setElements(organ
					.getReferencedFromCandidates(element));

			referencedFrom = new ArrayList<Element>();
			if (prototype != null) {
				if (prototype.getClass() == element.getClass()) {
					referencedFrom.addAll(prototype.getReferrer());
					elementsSelectionPanel.setSelectedElements(referencedFrom);
				}
			}
		}

		protected void changing() {
			referencedFrom = elementsSelectionPanel.getSelectedElements();

			super.changing();
		}
	}

	/**
	 * Show an element creation wizard in a dialog.
	 * 
	 * @param owner
	 *            owner of dialog
	 * @param organ
	 *            organ to add created element into
	 * @param prototype
	 *            element to use as prototype
	 */
	public static void showInDialog(Component owner, Organ organ,
			Element prototype) {

		WizardDialog dialog = WizardDialog.create(owner);


		dialog.setWizard(new CreateElementWizard(organ, prototype));

		config.get("dialog").read(dialog);
		dialog.setVisible(true);
		config.get("dialog").write(dialog);

		dialog.dispose();

		dialog.setWizard(null);
	}
}