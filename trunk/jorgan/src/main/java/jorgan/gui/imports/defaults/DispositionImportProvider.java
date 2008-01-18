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
package jorgan.gui.imports.defaults;

import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jorgan.disposition.Organ;
import jorgan.disposition.Rank;
import jorgan.gui.imports.spi.ImportProvider;
import jorgan.io.DispositionStream;
import jorgan.swing.FileSelector;
import jorgan.swing.GridBuilder;
import bias.Configuration;
import bias.swing.MessageBox;

/**
 * A provider for an import from a disposition.
 */
public class DispositionImportProvider implements ImportProvider {

	private static Configuration config = Configuration.getRoot().get(
			DispositionImportProvider.class);

	private OptionsPanel panel = new OptionsPanel();

	private String name;

	private String description;

	public DispositionImportProvider() {
		config.read(this);
	}

	public JPanel getOptionsPanel() {
		return panel;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean hasRanks() {
		File file = panel.fileSelector.getSelectedFile();

		return file != null && file.exists() && file.isFile();
	}

	public List<Rank> getRanks() {
		List<Rank> ranks = new ArrayList<Rank>();

		File file = panel.fileSelector.getSelectedFile();
		if (file != null) {
			try {
				ranks = readRanks(file);
			} catch (IOException ex) {
				panel.showMessage("exception/general", file.getPath());
			} catch (Exception ex) {
				panel.showMessage("exception/invalid", file.getPath());
			}
		}

		return ranks;
	}

	/**
	 * Read ranks from the given disposition file.
	 * 
	 * @param file
	 *            file to read from
	 * @return list of ranks
	 * @throws IOException
	 * @throws XMLFormatException
	 */
	private List<Rank> readRanks(File file) throws IOException {

		Organ organ = new DispositionStream().read(new FileInputStream(file));

		List<Rank> ranks = new ArrayList<Rank>(organ.getElements(Rank.class));
		for (Rank rank : ranks) {
			organ.removeElement(rank);
		}

		return ranks;
	}

	/**
	 * A panel for options.
	 */
	public class OptionsPanel extends JPanel {

		/**
		 * Insets to use by subclasse for a standard spacing around components.
		 */
		protected Insets insets = new Insets(2, 2, 2, 2);

		private FileSelector fileSelector = new FileSelector();

		/**
		 * Constructor.
		 */
		public OptionsPanel() {
			super(new GridBagLayout());

			GridBuilder builder = new GridBuilder(new double[] { 0.0d, 1.0d });

			builder.nextRow(1.0d);

			add(config.get("options/file").read(new JLabel()), builder
					.nextColumn());

			fileSelector.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					firePropertyChange("ranks", null, null);
				}
			});
			add(fileSelector, builder.nextColumn().fillHorizontal());
		}

		public void showMessage(String key, Object... args) {
			MessageBox box = config.get(key).read(
					new MessageBox(MessageBox.OPTIONS_OK));
			box.show(panel);
		}
	}
}