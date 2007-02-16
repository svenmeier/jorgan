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
import java.beans.PropertyChangeEvent;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;

import jorgan.disposition.Organ;
import jorgan.disposition.Stop;
import jorgan.gui.imports.spi.ImportProvider;
import jorgan.swing.wizard.AbstractPage;
import jorgan.swing.wizard.BasicWizard;
import jorgan.swing.wizard.WizardDialog;
import jorgan.util.I18N;

/**
 * A wizard for importing of sounds.
 */
public class ImportWizard extends BasicWizard {

	private static I18N i18n = I18N.get(ImportWizard.class);

	private Organ organ;

	private ImportProvider provider;

	private List<Stop> stops;

	private List<Stop> selectedStops;

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
		addPage(new StopSelectionPage());
	}

	/**
	 * Allows finish only if stops are selected.
	 * 
	 * @return <code>true</code> if stops are selected
	 */
	public boolean allowsFinish() {
		return selectedStops != null && selectedStops.size() > 0;
	}

	/**
	 * Finish.
	 */
	protected boolean finishImpl() {

		for (int s = 0; s < selectedStops.size(); s++) {
			organ.addElement(selectedStops.get(s));
		}

		return true;
	}

	/**
	 * Page for selection of an import provider.
	 */
	private class ProviderSelectionPage extends AbstractPage {

		private ProviderSelectionPanel providerSelectionPanel = new ProviderSelectionPanel();

		/**
		 * Constructor.
		 */
		public ProviderSelectionPage() {

			providerSelectionPanel.addPropertyChangeListener(this);
		}

		public String getDescription() {
			return i18n.getString("providerSelectionPage.description");
		}

		public JComponent getComponent() {
			return providerSelectionPanel;
		}

		public boolean allowsNext() {
			return providerSelectionPanel.getSelectedImportProvider() != null;
		}

		public boolean leavingToNext() {
			provider = providerSelectionPanel.getSelectedImportProvider();

			return true;
		}
	}

	/**
	 * Page for altering of options of the selected importMethod.
	 */
	private class ImportOptionsPage extends AbstractPage {

		private JPanel optionsPanel;

		public void enteringFromPrevious() {
			if (this.optionsPanel != null) {
				this.optionsPanel.removePropertyChangeListener(this);
			}

			this.optionsPanel = provider.getOptionsPanel();

			if (this.optionsPanel != null) {
				this.optionsPanel.addPropertyChangeListener(this);
			}
		}

		public String getDescription() {
			return provider.getDescription();
		}

		public JComponent getComponent() {
			return optionsPanel;
		}

		public boolean allowsNext() {
			return provider.hasStops();
		}

		public boolean leavingToNext() {
			stops = provider.getStops();

			return stops.size() > 0;
		}
	}

	/**
	 * Page for selecting of stops to import.
	 */
	private class StopSelectionPage extends AbstractPage {

		private StopSelectionPanel stopSelectionPanel = new StopSelectionPanel();

		/**
		 * Constructor.
		 */
		public StopSelectionPage() {
			stopSelectionPanel.addPropertyChangeListener(this);
		}

		public void enteringFromPrevious() {
			stopSelectionPanel.setStops(stops);
		}

		public String getDescription() {
			return i18n.getString("stopSelectionPage.description");
		}

		public JComponent getComponent() {
			return stopSelectionPanel;
		}

		public boolean leavingToPrevious() {
			selectedStops = null;

			return true;
		}

		public void propertyChange(PropertyChangeEvent evt) {
			selectedStops = stopSelectionPanel.getSelectedStops();

			super.propertyChange(evt);
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

		dialog.setTitle(i18n.getString("title"));

		dialog.setWizard(new ImportWizard(organ));

		dialog.start();

		dialog.dispose();

		dialog.setWizard(null);
	}
}