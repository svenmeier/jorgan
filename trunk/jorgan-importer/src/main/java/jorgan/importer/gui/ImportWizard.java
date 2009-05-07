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
package jorgan.importer.gui;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import jorgan.disposition.Element;
import jorgan.gui.construct.ElementsSelectionPanel;
import jorgan.play.Closed;
import jorgan.session.OrganSession;
import jorgan.session.undo.Compound;
import jorgan.swing.wizard.AbstractPage;
import jorgan.swing.wizard.BasicWizard;
import jorgan.swing.wizard.WizardDialog;
import bias.Configuration;

/**
 * A wizard for importing of elements.
 */
public class ImportWizard extends BasicWizard {

	private static Configuration config = Configuration.getRoot().get(
			ImportWizard.class);

	private OrganSession session;

	private Import aImport;

	private List<Element> elements;

	private List<Element> selectedElements;

	/**
	 * Create a new wizard.
	 * 
	 * @param organ
	 *            organ to import to
	 */
	public ImportWizard(OrganSession organ) {
		this.

		session = organ;

		addPage(new ProviderSelectionPage());
		addPage(new ImportOptionsPage());
		addPage(new ElementSelectionPage());
	}

	/**
	 * Allows finish only if ranks are selected.
	 * 
	 * @return <code>true</code> if ranks are selected
	 */
	@Override
	public boolean allowsFinish() {
		return selectedElements != null && selectedElements.size() > 0;
	}

	/**
	 * Finish.
	 */
	@Override
	protected boolean finishImpl() {

		session.getUndoManager().compound(new Compound() {
			public void run() {
				session.getPlay().closed(new Closed() {
					public void run() {
						session.getOrgan().addElements(selectedElements);
					}
				});
			}
		});

		return true;
	}

	/**
	 * Page for selection of an import provider.
	 */
	private class ProviderSelectionPage extends AbstractPage {

		private ImportSelectionPanel importSelectionPanel = new ImportSelectionPanel();

		public ProviderSelectionPage() {
			config.get("providerSelection").read(this);
		}

		@Override
		protected JComponent getComponentImpl() {
			return importSelectionPanel;
		}

		@Override
		public boolean allowsNext() {
			return importSelectionPanel.getSelectedImport() != null;
		}

		@Override
		public boolean leavingToNext() {
			aImport = importSelectionPanel.getSelectedImport();

			return true;
		}
	}

	/**
	 * Page for altering of options of the selected importMethod.
	 */
	private class ImportOptionsPage extends AbstractPage {

		@Override
		public String getDescription() {
			return aImport.getDescription();
		}

		@Override
		protected JComponent getComponentImpl() {
			return aImport.getOptionsPanel();
		}

		@Override
		public boolean allowsNext() {
			return aImport.hasElements();
		}

		@Override
		public boolean leavingToNext() {
			elements = aImport.getElements();

			return elements.size() > 0;
		}
	}

	/**
	 * Page for selecting of elements to import.
	 */
	private class ElementSelectionPage extends AbstractPage {

		private ElementsSelectionPanel selectionPanel = new ElementsSelectionPanel();

		public ElementSelectionPage() {
			config.get("elementSelection").read(this);
		}

		@Override
		public void enteringFromPrevious() {
			selectionPanel.setElements(new ArrayList<Element>(elements));
		}

		@Override
		protected JComponent getComponentImpl() {
			return selectionPanel;
		}

		@Override
		public boolean leavingToPrevious() {
			selectedElements = null;

			return true;
		}

		@Override
		protected void changing() {
			selectedElements = selectionPanel.getSelectedElements();

			super.changing();
		}
	}

	/**
	 * Show an import wizard in a dialog.
	 * 
	 * @param owner
	 *            owner of dialog
	 * @param organ
	 *            organ to import into
	 */
	public static void showInDialog(Component owner, OrganSession organ) {

		WizardDialog dialog = WizardDialog.create(owner);
		dialog.setWizard(new ImportWizard(organ));

		config.get("dialog").read(dialog);
		dialog.setVisible(true);
		config.get("dialog").write(dialog);

		dialog.dispose();

		dialog.setWizard(null);
	}
}