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
package jorgan.midimerger;

import java.util.HashSet;
import java.util.Set;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.spi.MidiDeviceProvider;

import jorgan.midimerger.merging.Merging;
import bias.Configuration;

/**
 * The provider of <code>MidiMerger</code> devices.
 * 
 * @see jorgan.midimerger.MidiMerger
 */
public class MidiMergerProvider extends MidiDeviceProvider {

	private static final String PREFIX = "Merger ";

	private static Configuration config = Configuration.getRoot().get(
			MidiMergerProvider.class);

	private Set<Merging> mergings = new HashSet<Merging>();

	public MidiMergerProvider() {
		config.read(this);
	}

	public void setMergings(Set<Merging> mergings) {
		this.mergings = mergings;
	}

	public Set<Merging> getMergings() {
		return mergings;
	}

	@Override
	public Info[] getDeviceInfo() {
		Info[] infos = new Info[mergings.size()];

		int i = 0;
		for (Merging merging : mergings) {
			infos[i] = new MergingInfo(merging);
			i++;
		}

		return infos;
	}

	@Override
	public boolean isDeviceSupported(Info info) {
		return info instanceof MergingInfo;
	}

	@Override
	public MidiDevice getDevice(Info info) {
		if (info instanceof MergingInfo) {
			MergingInfo mergingInfo = (MergingInfo) info;

			return new MidiMerger(info, mergingInfo.getMerging());
		}
		return null;
	}

	private class MergingInfo extends Info {

		private Merging merging;

		protected MergingInfo(Merging merging) {
			super(PREFIX + merging.getName(), "jOrgan",
					"Midi-Merger of jOrgan", "1.0");

			this.merging = merging;
		}

		public Merging getMerging() {
			return merging;
		}
	}

	public static boolean isMerger(String device) {
		return device.startsWith(PREFIX);
	}
}
