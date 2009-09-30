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
package jorgan.soundfont.gui.imports;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;

import jorgan.disposition.Element;
import jorgan.disposition.Rank;
import jorgan.disposition.Stop;
import jorgan.importer.gui.Import;
import jorgan.riff.RiffChunk;
import jorgan.riff.RiffFormatException;
import jorgan.soundfont.Preset;
import jorgan.soundfont.SoundfontReader;
import bias.Configuration;
import bias.swing.MessageBox;

/**
 * An {@link Import} from a SoundFont.
 */
public class SoundFontImport implements Import {

	static Configuration config = Configuration.getRoot().get(
			SoundFontImport.class);

	private OptionsPanel panel = new OptionsPanel();

	private String name;

	private String description;

	public SoundFontImport() {
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

	public boolean hasElements() {
		File file = panel.getSelectedFile();

		return file != null && file.exists() && file.isFile();
	}

	public List<Element> getElements() {
		List<Element> elements = new ArrayList<Element>();

		File file = panel.getSelectedFile();
		int bank = panel.getBank();
		if (file != null) {
			try {
				Set<Rank> ranks = readRanks(file, bank);

				elements.addAll(ranks);

				if (panel.getCreateStops()) {
					for (Rank rank : ranks) {
						Stop stop = new Stop();
						stop.setName(rank.getName());
						stop.reference(rank);
						elements.add(stop);
					}
				}
			} catch (RiffFormatException ex) {
				showMessage("exception/invalid", file.getPath());
			} catch (IOException ex) {
				showMessage("exception/general", file.getPath());
			}
		}

		return elements;
	}

	private void showMessage(String key, Object... args) {
		MessageBox box = SoundFontImport.config.get(key).read(
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
	private Set<Rank> readRanks(File file, int bank) throws IOException,
			RiffFormatException {

		Set<Rank> ranks = new HashSet<Rank>();

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
				rank.setBank(bank);
				if (!this.panel.getTouchSensitive()) {
					rank.setVelocity(100);
				}
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