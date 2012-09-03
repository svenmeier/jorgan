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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;

import jorgan.exporter.gui.spi.ExportRegistry;
import jorgan.exporter.target.Target;
import jorgan.session.OrganSession;
import jorgan.swing.wizard.AbstractPage;
import jorgan.swing.wizard.BasicWizard;
import jorgan.swing.wizard.Page;
import jorgan.swing.wizard.WizardDialog;
import bias.Configuration;
import bias.swing.MessageBox;

/**
 * A wizard for importing of elements.
 */
public class ExportWizard extends BasicWizard {

	private static Logger log = Logger.getLogger(ExportWizard.class.getName());

	private static Configuration config = Configuration.getRoot().get(
			ExportWizard.class);

	private OrganSession session;

	private Export aExport;

	private List<Page> exportPages = new ArrayList<Page>();

	private TargetPanel targetPanel = new TargetPanel();

	private boolean hasTarget = false;

	/**
	 * Create a new wizard.
	 * 
	 * @param session
	 *            organ to import to
	 */
	public ExportWizard(OrganSession session) {
		this.session = session;

		addPage(new ExportSelectionPage());
		addPage(new TargetPage());
	}

	/**
	 * Allows finish only if target is selected.
	 */
	@Override
	public boolean allowsFinish() {
		return hasTarget;
	}

	/**
	 * Finish.
	 */
	@Override
	protected boolean finishImpl() {
		Target target = targetPanel.getTarget();
		if (target == null) {
			return false;
		}

		try {
			target.export(aExport);
		} catch (IOException e) {
			log.log(Level.INFO, e.getMessage(), e);

			MessageBox box = config.get("exportException").read(
					new MessageBox(MessageBox.OPTIONS_OK));
			box.show(targetPanel);

			return false;
		}

		return true;
	}

	/**
	 * Page for selection of an export.
	 */
	private class ExportSelectionPage extends AbstractPage {

		private ExportSelectionPanel selectionPanel = new ExportSelectionPanel();

		public ExportSelectionPage() {
			config.get("exportSelection").read(this);

			selectionPanel.setExports(ExportRegistry.getExports(session));
		}

		@Override
		protected JComponent getComponentImpl() {
			return selectionPanel;
		}

		@Override
		public boolean allowsNext() {
			return selectionPanel.getSelectedExport() != null;
		}

		@Override
		public boolean leavingToNext() {
			setExport(selectionPanel.getSelectedExport());

			return true;
		}
	}

	/**
	 * Page for the target of export.
	 */
	private class TargetPage extends AbstractPage {

		public TargetPage() {
			config.get("target").read(this);
		}

		@Override
		public boolean leavingToPrevious() {
			hasTarget = false;

			return true;
		}

		@Override
		protected JComponent getComponentImpl() {
			return targetPanel;
		}

		@Override
		protected void changing() {
			hasTarget = targetPanel.hasTarget();

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

	public void setExport(Export aExport) {
		for (Page page : exportPages) {
			removePage(page);
		}
		exportPages.clear();

		this.aExport = aExport;

		exportPages.addAll(aExport.getPages());
		Collections.reverse(exportPages);
		for (Page page : exportPages) {
			addPage(1, page);
		}
	}
}