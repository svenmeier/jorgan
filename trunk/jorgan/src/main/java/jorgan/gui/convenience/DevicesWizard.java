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
package jorgan.gui.convenience;

import java.awt.Component;

import javax.swing.JComponent;

import jorgan.disposition.Input;
import jorgan.disposition.Organ;
import jorgan.disposition.Output;
import jorgan.midi.Direction;
import jorgan.swing.wizard.AbstractPage;
import jorgan.swing.wizard.BasicWizard;
import jorgan.swing.wizard.WizardDialog;
import bias.Configuration;

/**
 * A wizard for configuring of {@link Input}s and {@link Output}s.
 */
public class DevicesWizard extends BasicWizard {

	private static Configuration config = Configuration.getRoot().get(
			DevicesWizard.class);

	private Organ organ;

	private InputPage inputPage;

	private OutputPage outputPage;

	/**
	 * Create a new wizard.
	 * 
	 * @param organ
	 *            organ to import to
	 */
	public DevicesWizard(Organ organ) {
		this.organ = organ;

		inputPage = new InputPage();
		addPage(inputPage);
		
		outputPage = new OutputPage();
		addPage(outputPage);
	}

	/**
	 * Finish.
	 */
	@Override
	protected boolean finishImpl() {

		inputPage.write();
		outputPage.write();

		return true;
	}

	private class InputPage extends AbstractPage {
		
		private DevicesPanel devicesPanel;

		public InputPage() {
			config.get("input").read(this);
			
			devicesPanel = new DevicesPanel(organ, Direction.IN);
		}
		
		@Override
		protected JComponent getComponentImpl() {
			return devicesPanel;
		}
		
		public void write() {
			devicesPanel.write();
		}
	}

	private class OutputPage extends AbstractPage {
		
		private DevicesPanel devicesPanel;

		public OutputPage() {
			config.get("output").read(this);
			
			devicesPanel = new DevicesPanel(organ, Direction.OUT);
		}

		@Override
		protected JComponent getComponentImpl() {
			return devicesPanel;
		}
		
		public void write() {
			devicesPanel.write();
		}
	}

	/**
	 * Show wizard in a dialog.
	 * 
	 * @param owner
	 *            owner of dialog
	 * @param organ
	 *            organ to configure
	 */
	public static void showInDialog(Component owner, Organ organ) {

		WizardDialog dialog = WizardDialog.create(owner);
		dialog.setWizard(new DevicesWizard(organ));

		config.get("dialog").read(dialog);
		dialog.setVisible(true);
		config.get("dialog").write(dialog);

		dialog.dispose();

		dialog.setWizard(null);
	}
}