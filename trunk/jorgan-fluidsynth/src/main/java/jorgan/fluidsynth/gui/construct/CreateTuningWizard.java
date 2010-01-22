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
package jorgan.fluidsynth.gui.construct;

import java.awt.Component;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;

import jorgan.fluidsynth.disposition.FluidsynthSound;
import jorgan.fluidsynth.disposition.Tuning;
import jorgan.fluidsynth.io.TuningsStream;
import jorgan.gui.undo.Compound;
import jorgan.gui.undo.UndoManager;
import jorgan.session.OrganSession;
import jorgan.swing.wizard.AbstractPage;
import jorgan.swing.wizard.BasicWizard;
import jorgan.swing.wizard.WizardDialog;
import bias.Configuration;

/**
 * A wizard for creating of a {@link Tuning}.
 */
public class CreateTuningWizard extends BasicWizard {

	private static final Logger logger = Logger
			.getLogger(CreateTuningWizard.class.getName());

	private static Configuration config = Configuration.getRoot().get(
			CreateTuningWizard.class);

	private FluidsynthSound sound;

	private List<Tuning> tunings = new ArrayList<Tuning>();

	private OrganSession session;

	/**
	 * Create a new wizard.
	 */
	public CreateTuningWizard(OrganSession session, FluidsynthSound sound) {
		this.session = session;
		this.sound = sound;

		addPage(new TuningPage());
	}

	/**
	 * Allows finish only if a tuning isselected.
	 * 
	 * @return <code>true</code> if tuning is selected
	 */
	@Override
	public boolean allowsFinish() {
		return !tunings.isEmpty();
	}

	@Override
	protected boolean finishImpl() {
		session.lookup(UndoManager.class).compound(new Compound() {
			@Override
			public void run() {
				for (Tuning tuning : tunings) {
					sound.addTuning(tuning);
				}
			}
		});

		return true;
	}

	/**
	 * Page for selecting a tuning.
	 */
	private class TuningPage extends AbstractPage {

		private TuningCreationPanel tuningPanel = new TuningCreationPanel();

		private TuningPage() {
			config.get("tuning").read(this);

			tuningPanel.setTunings(readTunings());
		}

		@Override
		protected JComponent getComponentImpl() {
			return tuningPanel;
		}

		@Override
		protected void changing() {
			tunings = tuningPanel.getTunings();

			super.changing();
		}
	}

	private List<Tuning> readTunings() {
		List<Tuning> tunings;

		TuningsStream stream = new TuningsStream();
		try {
			InputStream input = getClass().getResourceAsStream("/tunings.xml");
			try {
				tunings = stream.read(input);
			} finally {
				input.close();
			}
		} catch (IOException e) {
			logger.log(Level.WARNING, e.getMessage(), e);

			tunings = Collections.emptyList();
		}

		return tunings;
	}

	/**
	 * Show an {@link Tuning} creation wizard in a dialog.
	 * 
	 * @param owner
	 *            owner of dialog
	 */
	public static void showInDialog(Component owner, OrganSession session,
			FluidsynthSound sound) {

		WizardDialog dialog = WizardDialog.create(owner);
		dialog.setWizard(new CreateTuningWizard(session, sound));

		config.get("dialog").read(dialog);
		dialog.setVisible(true);
		config.get("dialog").write(dialog);

		dialog.dispose();

		dialog.setWizard(null);
	}
}