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
package jorgan.creative.gui.imports;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;

import jorgan.creative.SoundFontManager;
import jorgan.disposition.Element;
import jorgan.disposition.Rank;
import jorgan.disposition.Stop;
import jorgan.importer.gui.Import;
import jorgan.midi.DevicePool;
import jorgan.midi.Direction;
import jorgan.swing.wizard.AbstractPage;
import jorgan.swing.wizard.Page;
import bias.Configuration;

/**
 * A {@link Import} of {@link Rank}s directly from Creative soundcards.
 * 
 * @see jorgan.creative.SoundFontManager
 */
public class CreativeImport implements Import {

	static Configuration config = Configuration.getRoot().get(
			CreativeImport.class);

	private OptionsPanel panel;

	private String name;

	private String description;

	private List<Element> elements;

	public CreativeImport() {
		config.read(this);
	}

	@Override
	public List<Page> getPages() {
		return Collections.<Page> singletonList(new OptionsPage());
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public List<Element> getElements() {
		return elements;
	}

	private Set<Rank> readRanks(Bank bank) throws IOException {
		Set<Rank> ranks = new HashSet<Rank>();

		SoundFontManager manager = new SoundFontManager(panel
				.getSelectedDevice().name);

		for (int p = 0; p < 128; p++) {
			try {
				String preset = manager.getPresetDescriptor(bank.number, p);
				if (preset != null && !"".equals(preset)) {
					Rank rank = new Rank();
					rank.setName(preset);
					rank.setProgram(p);
					rank.setBank(bank.number);
					if (!this.panel.getTouchSensitive()) {
						rank.setVelocity(100);
					}
					ranks.add(rank);
				}
			} catch (IllegalArgumentException invalidPreset) {
			}
		}

		manager.destroy();

		return ranks;
	}

	private class OptionsPage extends AbstractPage {

		@Override
		public String getDescription() {
			return CreativeImport.this.getDescription();
		}

		@Override
		protected JComponent getComponentImpl() {
			if (panel == null) {
				List<Device> devices = new ArrayList<Device>();

				for (String deviceName : DevicePool.instance()
						.getMidiDeviceNames(Direction.OUT)) {
					SoundFontManager manager;
					try {
						manager = new SoundFontManager(deviceName);
					} catch (IOException noCreativeDevice) {
						continue;
					}

					Device device = new Device(deviceName);
					devices.add(device);

					for (int b = 0; b < 127; b++) {
						try {
							if (manager.isLoaded(b)) {
								device.banks.add(new Bank(b, manager
										.getDescriptor(b)));
							}
						} catch (IllegalArgumentException ex) {
							// bank is illegal??
						}
					}

					manager.destroy();
				}

				panel = new OptionsPanel(devices);
			}
			return panel;
		}

		@Override
		public boolean leavingToPrevious() {
			return elements == null;
		}

		@Override
		public boolean allowsNext() {
			return panel.getSelectedBank() != null;
		}

		@Override
		public boolean leavingToNext() {
			try {
				List<Element> elements = new ArrayList<Element>();

				Set<Rank> ranks = readRanks(panel.getSelectedBank());
				elements.addAll(ranks);

				if (panel.getCreateStops()) {
					for (Rank rank : ranks) {
						Stop stop = new Stop();
						stop.setName(rank.getName());
						stop.reference(rank);
						elements.add(stop);
					}
				}

				CreativeImport.this.elements = elements;
			} catch (IOException ex) {
				return false;
			}

			return true;
		}
	}
}