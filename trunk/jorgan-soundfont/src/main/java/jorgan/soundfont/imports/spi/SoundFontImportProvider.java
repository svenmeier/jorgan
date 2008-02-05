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
package jorgan.soundfont.imports.spi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JPanel;

import jorgan.disposition.Rank;
import jorgan.gui.imports.spi.ImportProvider;
import jorgan.riff.RiffChunk;
import jorgan.riff.RiffFormatException;
import jorgan.soundfont.Preset;
import jorgan.soundfont.SoundfontReader;
import jorgan.soundfont.imports.OptionsPanel;
import bias.Configuration;
import bias.swing.MessageBox;

/**
 * A provider for an import from a SoundFont.
 */
public class SoundFontImportProvider implements ImportProvider {

	static Configuration config = Configuration.getRoot().get(
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

	public boolean hasRanks() {
		File file = panel.getSelectedFile();

		return file != null && file.exists() && file.isFile();
	}

	public List<Rank> getRanks() {
		List<Rank> ranks = new ArrayList<Rank>();

		File file = panel.getSelectedFile();
		if (file != null) {
			try {
				ranks = readRanks(file);
			} catch (RiffFormatException ex) {
				showMessage("exception/invalid", file.getPath());
			} catch (IOException ex) {
				showMessage("exception/general", file.getPath());
			}
		}

		return ranks;
	}

	private void showMessage(String key, Object... args) {
		MessageBox box = SoundFontImportProvider.config.get(key).read(
				new MessageBox(MessageBox.OPTIONS_OK));
		box.show(panel);
	}

	/**
	 * Read ranks from the given soundfont file.
	 * 
	 * @param file
	 *            file to read from
	 * @return list of ranks
	 * @throws IOException
	 * @throws RiffFormatException
	 */
	private List<Rank> readRanks(File file) throws IOException,
			RiffFormatException {

		ArrayList<Rank> ranks = new ArrayList<Rank>();

		InputStream input = null;
		try {
			input = new FileInputStream(file);

			RiffChunk riffChunk = new SoundfontReader(input).read();

			List<Preset> presets = SoundfontReader.getPresets(riffChunk);
			Collections.sort(presets);
			for (int p = 0; p < presets.size(); p++) {
				Preset preset = presets.get(p);

				Rank rank = new Rank();
				rank.setName(preset.getName());
				rank.setProgram(preset.getProgram());
				ranks.add(rank);
			}
		} finally {
			if (input != null) {
				input.close();
			}
		}

		return ranks;
	}
}