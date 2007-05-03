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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jorgan.disposition.Stop;
import jorgan.gui.imports.spi.ImportProvider;
import jorgan.io.riff.RiffChunk;
import jorgan.io.riff.RiffFormatException;
import jorgan.io.soundfont.Preset;
import jorgan.io.soundfont.SoundfontReader;
import jorgan.swing.FileSelector;
import jorgan.swing.GridBuilder;
import bias.Configuration;
import bias.swing.MessageBox;

/**
 * A provider for an import from a SoundFont.
 */
public class SoundFontImportProvider implements ImportProvider {

	private static Configuration config = Configuration.getRoot().get(
			SoundFontImportProvider.class);

	private OptionsPanel panel = new OptionsPanel();

	private String name;

	private String description;

	public SoundFontImportProvider() {
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

	public boolean hasStops() {
		File file = panel.fileSelector.getSelectedFile();

		return file != null && file.exists() && file.isFile();
	}

	public List<Stop> getStops() {
		List<Stop> stops = new ArrayList<Stop>();

		File file = panel.fileSelector.getSelectedFile();
		if (file != null) {
			try {
				stops = readStops(file);
			} catch (RiffFormatException ex) {
				panel.showMessage("exception/invalid", file.getPath());
			} catch (IOException ex) {
				panel.showMessage("exception/general", file.getPath());
			}
		}

		return stops;
	}

	/**
	 * Read stops from the given soundfont file.
	 * 
	 * @param file
	 *            file to read from
	 * @return list of stops
	 * @throws IOException
	 * @throws RiffFormatException
	 */
	private List<Stop> readStops(File file) throws IOException,
			RiffFormatException {

		ArrayList<Stop> stops = new ArrayList<Stop>();

		InputStream input = null;
		try {
			input = new FileInputStream(file);

			RiffChunk riffChunk = new SoundfontReader(input).read();

			List<Preset> presets = SoundfontReader.getPresets(riffChunk);
			Collections.sort(presets);
			for (int p = 0; p < presets.size(); p++) {
				Preset preset = presets.get(p);

				Stop stop = new Stop();
				stop.setName(preset.getName());
				stop.setProgram(preset.getProgram());
				stops.add(stop);
			}
		} finally {
			if (input != null) {
				input.close();
			}
		}

		return stops;
	}

	/**
	 * A panel for options.
	 */
	public class OptionsPanel extends JPanel {

		/**
		 * Insets to use by subclasse for a standard spacing around components.
		 */
		protected Insets insets = new Insets(2, 2, 2, 2);

		private JLabel fileLabel = new JLabel();

		private FileSelector fileSelector = new FileSelector();

		/**
		 * Constructor.
		 */
		public OptionsPanel() {
			super(new GridBagLayout());

			GridBuilder builder = new GridBuilder(new double[] { 0.0d, 1.0d });

			builder.nextRow(1.0d);

			config.get("optionsPanel/fileLabel").read(fileLabel);
			add(fileLabel, builder.nextColumn());

			fileSelector.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					firePropertyChange("stops", null, null);
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