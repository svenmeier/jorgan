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
package jorgan.creative.gui.imports.spi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;

import jorgan.creative.SoundFontManager;
import jorgan.creative.gui.imports.Bank;
import jorgan.creative.gui.imports.Device;
import jorgan.creative.gui.imports.OptionsPanel;
import jorgan.disposition.Element;
import jorgan.disposition.Rank;
import jorgan.disposition.Stop;
import jorgan.gui.imports.spi.ImportProvider;
import bias.Configuration;

/**
 * A provider that is capable to import {@link Rank}s directly from Creative
 * soundcards.
 * 
 * @see jorgan.creative.SoundFontManager
 */
public class CreativeImportProvider implements ImportProvider {

	static Configuration config = Configuration.getRoot().get(
			CreativeImportProvider.class);

	private OptionsPanel panel;

	private Device[] devices = new Device[0];

	private String name;

	private String description;

	public CreativeImportProvider() {
		config.read(this);

		SoundFontManager manager = new SoundFontManager();

		devices = new Device[manager.getNumDevices()];
		for (int d = 0; d < devices.length; d++) {
			devices[d] = new Device(manager.getDeviceName(d));

			for (int b = 0; b < 127; b++) {
				try {
					if (manager.isBankUsed(d, b)) {
						devices[d].banks.add(new Bank(b, manager
								.getBankDescriptor(d, b)));
					}
				} catch (IllegalArgumentException ex) {
					// bank is illegal??
				}
			}
		}
	}

	public JPanel getOptionsPanel() {
		if (panel == null) {
			panel = new OptionsPanel(devices);
		}
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
		return panel.getSelectedBank() != null;
	}

	public List<Element> getElements() {
		List<Element> elements = new ArrayList<Element>();

		Bank bank = panel.getSelectedBank();
		if (bank != null) {
			Set<Rank> ranks = readRanks(bank);
			elements.addAll(ranks);

			if (panel.getCreateStops()) {
				for (Rank rank : ranks) {
					Stop stop = new Stop();
					stop.setName(rank.getName());
					stop.reference(rank);
					elements.add(stop);
				}
			}
		}

		return elements;
	}

	private Set<Rank> readRanks(Bank bank) {
		Set<Rank> ranks = new HashSet<Rank>();

		SoundFontManager manager = new SoundFontManager();

		for (int p = 0; p < 128; p++) {
			try {
				String preset = manager.getPresetDescriptor(0, bank.number, p);
				if (preset != null && !"".equals(preset)) {
					Rank rank = new Rank();
					rank.setName(preset);
					rank.setProgram(p);
					rank.setBank(bank.number);
					ranks.add(rank);
				}
			} catch (IllegalArgumentException invalidPreset) {
			}
		}

		return ranks;
	}
}