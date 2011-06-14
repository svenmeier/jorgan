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
package jorgan.exporter.gui;

import java.awt.Component;

import javax.swing.JComponent;

import jorgan.gui.undo.Compound;
import jorgan.gui.undo.UndoManager;
import jorgan.play.Closed;
import jorgan.play.OrganPlay;
import jorgan.session.OrganSession;
import jorgan.swing.wizard.AbstractPage;
import jorgan.swing.wizard.BasicWizard;
import jorgan.swing.wizard.WizardDialog;
import bias.Configuration;

/**
 * A wizard for importing of elements.
 */
public class ExportWizard extends BasicWizard {

	private static Configuration config = Configuration.getRoot().get(
			ExportWizard.class);

	private OrganSession session;

	private Export aExport;

	/**
	 * Create a new wizard.
	 * 
	 * @param organ
	 *            organ to import to
	 */
	public ExportWizard(OrganSession organ) {
		this.

		session = organ;

		addPage(new ProviderSelectionPage());
		addPage(new ImportOptionsPage());
		addPage(new TargetPage());
	}

	/**
	 * Allows finish only if ranks are selected.
	 * 
	 * @return <code>true</code> if ranks are selected
	 */
	@Override
	public boolean allowsFinish() {
		// TODO
		return false;
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
						// TODO
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

		private ExportSelectionPanel selectionPanel = new ExportSelectionPanel();

		public ProviderSelectionPage() {
			config.get("providerSelection").read(this);
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
			aExport = selectionPanel.getSelectedImport();

			return true;
		}
	}

	/**
	 * Page for altering of options of the selected exportMethod.
	 */
	private class ImportOptionsPage extends AbstractPage {

		@Override
		public String getDescription() {
			return aExport.getDescription();
		}

		@Override
		protected JComponent getComponentImpl() {
			return aExport.getOptionsPanel();
		}

		@Override
		public boolean allowsNext() {
			return aExport.hasElements();
		}

		@Override
		public boolean leavingToNext() {
			// TODO
			return true;
		}
	}

	/**
	 * Page for the target of export.
	 */
	private class TargetPage extends AbstractPage {

		private TargetPanel targetPanel = new TargetPanel();

		public TargetPage() {
			config.get("target").read(this);
		}

		@Override
		public void enteringFromPrevious() {
		}

		@Override
		protected JComponent getComponentImpl() {
			return targetPanel;
		}

		@Override
		public boolean leavingToPrevious() {
			return true;
		}

		@Override
		protected void changing() {
			super.changing();
		}
	}

	/**
	 * Show an export wizard in a dialog.
	 * 
	 * @param owner
	 *            owner of dialog
	 * @param organ
	 *            organ to export from
	 */
	public static void showInDialog(Component owner, OrganSession organ) {

		WizardDialog dialog = WizardDialog.create(owner);
		dialog.setWizard(new ExportWizard(organ));

		config.get("dialog").read(dialog);
		dialog.setVisible(true);
		config.get("dialog").write(dialog);

		dialog.dispose();

		dialog.setWizard(null);
	}
}