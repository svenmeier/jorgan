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
import jorgan.gui.undo.Compound;
import jorgan.gui.undo.UndoManager;
import jorgan.importer.gui.spi.ImportRegistry;
import jorgan.play.Closed;
import jorgan.play.OrganPlay;
import jorgan.session.OrganSession;
import jorgan.swing.wizard.AbstractPage;
import jorgan.swing.wizard.BasicWizard;
import jorgan.swing.wizard.Page;
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

	private List<Page> importPages = new ArrayList<Page>();

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

		addPage(new ImportSelectionPage());
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

		session.lookup(UndoManager.class).compound(new Compound() {
			public void run() {
				session.lookup(OrganPlay.class).closed(new Closed() {
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
	private class ImportSelectionPage extends AbstractPage {

		private ImportSelectionPanel selectionPanel = new ImportSelectionPanel();

		public ImportSelectionPage() {
			config.get("importSelection").read(this);

			selectionPanel.setImports(ImportRegistry.getImports());
		}

		@Override
		protected JComponent getComponentImpl() {
			return selectionPanel;
		}

		@Override
		public boolean allowsNext() {
			return selectionPanel.getSelectedImport() != null;
		}

		@Override
		public boolean leavingToNext() {
			setImport(selectionPanel.getSelectedImport());

			return true;
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
			List<Element> elements = aImport.getElements();

			selectionPanel.setElements(elements);
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

	public void setImport(Import aImport) {
		for (Page page : importPages) {
			removePage(page);
		}
		importPages.clear();

		this.aImport = aImport;

		importPages.addAll(aImport.getPages());
		for (Page page : importPages) {
			addPage(1, page);
		}
	}
}