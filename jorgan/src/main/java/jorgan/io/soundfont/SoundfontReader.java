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
package jorgan.io.soundfont;

import java.io.InputStream;
import java.util.List;

import jorgan.io.riff.ListChunk;
import jorgan.io.riff.RiffChunk;
import jorgan.io.riff.RiffReader;
import jorgan.io.soundfont.factory.PresetHeaderChunkFactory;

/**
 * A reader for data in <em>Soundfont</em> file format.
 */
public class SoundfontReader extends RiffReader {

	public static final String PRESETS_DATA_LIST_TYPE = "pdta";

	public static final String PRESETS_HEADER_ID = "phdr";

	public SoundfontReader(InputStream in) {
		super(in);

		registerFactory(PRESETS_HEADER_ID, new PresetHeaderChunkFactory());
	}

	/**
	 * Check if read riffChunk contains the demanded presets list.
	 * 
	 * @param chunk
	 *            riffChunk to check
	 */
	protected void checkFormat(RiffChunk chunk) throws SoundfontFormatException {
		try {
			getPresets(chunk);
		} catch (Exception ex) {
			throw new SoundfontFormatException("missing soundfont presets");
		}
	}

	/**
	 * Utility method to get a list of presets contained in the given soundfont.
	 * 
	 * @param chunk
	 *            riffChunk of soundfont
	 * @return list of presets
	 */
	public static List<Preset> getPresets(RiffChunk chunk) {
		ListChunk presetDataChunk = chunk
				.getListChunk(SoundfontReader.PRESETS_DATA_LIST_TYPE);
		PresetHeaderChunk presetHeaderChunk = (PresetHeaderChunk) presetDataChunk
				.getChunk(SoundfontReader.PRESETS_HEADER_ID);

		return presetHeaderChunk.getPresets();
	}
}
