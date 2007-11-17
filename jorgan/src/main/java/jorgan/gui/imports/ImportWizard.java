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
package jorgan.gui.imports;

import java.awt.Component;
import java.util.List;

import javax.swing.JComponent;

import jorgan.disposition.Organ;
import jorgan.disposition.Rank;
import jorgan.gui.imports.spi.ImportProvider;
import jorgan.swing.wizard.AbstractPage;
import jorgan.swing.wizard.BasicWizard;
import jorgan.swing.wizard.WizardDialog;
import bias.Configuration;

/**
 * A wizard for importing of sounds.
 */
public class ImportWizard extends BasicWizard {

	private static Configuration config = Configuration.getRoot().get(
			ImportWizard.class);

	private Organ organ;

	private ImportProvider provider;

	private List<Rank> ranks;

	private List<Rank> selectedRanks;

	/**
	 * Create a new wizard.
	 * 
	 * @param organ
	 *            organ to import to
	 */
	public ImportWizard(Organ organ) {
		this.organ = organ;

		addPage(new ProviderSelectionPage());
		addPage(new ImportOptionsPage());
		addPage(new RankSelectionPage());
	}

	/**
	 * Allows finish only if ranks are selected.
	 * 
	 * @return <code>true</code> if ranks are selected
	 */
	@Override
	public boolean allowsFinish() {
		return selectedRanks != null && selectedRanks.size() > 0;
	}

	/**
	 * Finish.
	 */
	@Override
	protected boolean finishImpl() {

		for (int s = 0; s < selectedRanks.size(); s++) {
			organ.addElement(selectedRanks.get(s));
		}

		return true;
	}

	/**
	 * Page for selection of an import provider.
	 */
	private class ProviderSelectionPage extends AbstractPage {

		private ProviderSelectionPanel providerSelectionPanel = new ProviderSelectionPanel();

		public ProviderSelectionPage() {
			config.get("providerSelection").read(this);
		}

		@Override
		protected JComponent getComponentImpl() {
			return providerSelectionPanel;
		}

		@Override
		public boolean allowsNext() {
			return providerSelectionPanel.getSelectedImportProvider() != null;
		}

		@Override
		public boolean leavingToNext() {
			provider = providerSelectionPanel.getSelectedImportProvider();

			return true;
		}
	}

	/**
	 * Page for altering of options of the selected importMethod.
	 */
	private class ImportOptionsPage extends AbstractPage {

		@Override
		public String getDescription() {
			return provider.getDescription();
		}

		@Override
		protected JComponent getComponentImpl() {
			return provider.getOptionsPanel();
		}

		@Override
		public boolean allowsNext() {
			return provider.hasRanks();
		}

		@Override
		public boolean leavingToNext() {
			ranks = provider.getRanks();

			return ranks.size() > 0;
		}
	}

	/**
	 * Page for selecting of ranks to import.
	 */
	private class RankSelectionPage extends AbstractPage {

		private RankSelectionPanel rankSelectionPanel = new RankSelectionPanel();

		public RankSelectionPage() {
			config.get("rankSelection").read(this);
		}

		@Override
		public void enteringFromPrevious() {
			rankSelectionPanel.setRanks(ranks);
		}

		@Override
		protected JComponent getComponentImpl() {
			return rankSelectionPanel;
		}

		@Override
		public boolean leavingToPrevious() {
			selectedRanks = null;

			return true;
		}

		@Override
		protected void changing() {
			selectedRanks = rankSelectionPanel.getSelectedRanks();

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
	public static void showInDialog(Component owner, Organ organ) {

		WizardDialog dialog = WizardDialog.create(owner);
		dialog.setWizard(new ImportWizard(organ));

		config.get("dialog").read(dialog);
		dialog.setVisible(true);
		config.get("dialog").write(dialog);

		dialog.dispose();

		dialog.setWizard(null);
	}
}