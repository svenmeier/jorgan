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
import jorgan.disposition.spi.ElementRegistry;
import jorgan.session.OrganSession;
import jorgan.session.undo.Compound;
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

	private OrganSession session;

	private Element element;

	private List<Element> referencesTo = new ArrayList<Element>();

	private List<Element> referencedFrom = new ArrayList<Element>();

	/**
	 * Create a new wizard.
	 * 
	 * @param organ
	 *            the organ to creat element for
	 */
	public CreateElementWizard(OrganSession organ) {
		this.session = organ;

		addPage(new ElementPage());
		addPage(new ReferencesToPage());
		addPage(new ReferencedByPage());
	}

	/**
	 * Allows finish only if element is created.
	 * 
	 * @return <code>true</code> if stops are selected
	 */
	@Override
	public boolean allowsFinish() {
		return element != null;
	}

	/**
	 * Finish.
	 */
	@Override
	protected boolean finishImpl() {

		session.getUndoManager().compound(new Compound() {
			public void run() {
				session.getOrgan().addElement(element);

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
			}
		});
			
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
			config.get("element").read(this);

			elementPanel.setElementClasses(ElementRegistry.getElementClasses());
		}

		@Override
		protected JComponent getComponentImpl() {
			return elementPanel;
		}

		@Override
		public boolean allowsNext() {
			return element != null;
		}

		@Override
		protected void changing() {
			Class<? extends Element> elementClass = elementPanel
					.getElementClass();
			String elementName = elementPanel.getElementName();

			if (elementClass != null && elementName != null) {
				try {
					element = elementClass.newInstance();
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
			config.get("referencesTo").read(this);
		}

		@Override
		protected JComponent getComponentImpl() {
			return elementsSelectionPanel;
		}

		@Override
		public void enteringFromPrevious() {
			elementsSelectionPanel.setElements(session.getOrgan()
					.getReferenceToCandidates(element));

			referencesTo = new ArrayList<Element>();
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

		public ReferencedByPage() {
			config.get("referencedBy").read(this);
		}

		@Override
		protected JComponent getComponentImpl() {
			return elementsSelectionPanel;
		}

		@Override
		public void enteringFromPrevious() {
			elementsSelectionPanel.setElements(session.getOrgan()
					.getReferencedFromCandidates(element));

			referencedFrom = new ArrayList<Element>();
		}

		@Override
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
	 */
	public static void showInDialog(Component owner, OrganSession organ) {

		WizardDialog dialog = WizardDialog.create(owner);

		dialog.setWizard(new CreateElementWizard(organ));

		config.get("dialog").read(dialog);
		dialog.setVisible(true);
		config.get("dialog").write(dialog);

		dialog.dispose();

		dialog.setWizard(null);
	}
}