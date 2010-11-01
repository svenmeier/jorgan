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
package jorgan.customizer.gui;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import jorgan.customizer.gui.spi.CustomizerRegistry;
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
 * A wizard for customizing of a disposition.
 */
public class CustomizeWizard extends BasicWizard {

	private static Configuration config = Configuration.getRoot().get(
			CustomizeWizard.class);

	private List<Customizer> customizers = new ArrayList<Customizer>();

	private OrganSession session;

	/**
	 * Create a new wizard.
	 * 
	 * @param session
	 *            organ to import to
	 */
	public CustomizeWizard(OrganSession session) {
		this.session = session;

		for (Customizer customizer : CustomizerRegistry
				.lookupCustomizers(session)) {
			addCustomizer(customizer);
		}
	}

	private void addCustomizer(Customizer customizer) {
		customizers.add(customizer);

		CustomizerPage page = new CustomizerPage(customizer);
		addPage(page);

		if (getCurrentPage() == null) {
			setCurrentPage(page);
		}
	}

	private class CustomizerPage extends AbstractPage {

		private Customizer customizer;

		public CustomizerPage(Customizer customizer) {
			this.customizer = customizer;
		}

		@Override
		public String getDescription() {
			return customizer.getDescription();
		}

		@Override
		protected JComponent getComponentImpl() {
			return customizer.getComponent();
		}
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
						for (Customizer customizer : customizers) {
							customizer.apply();
						}
					}
				});
			}
		});

		return true;
	}

	/**
	 * Show wizard in a dialog.
	 * 
	 * @param owner
	 *            owner of dialog
	 * @param session
	 *            organ to configure
	 */
	public static void showInDialog(Component owner, OrganSession session) {

		WizardDialog dialog = WizardDialog.create(owner);
		dialog.setWizard(new CustomizeWizard(session));

		config.get("dialog").read(dialog);
		dialog.setVisible(true);
		config.get("dialog").write(dialog);

		dialog.dispose();

		dialog.setWizard(null);
	}
}