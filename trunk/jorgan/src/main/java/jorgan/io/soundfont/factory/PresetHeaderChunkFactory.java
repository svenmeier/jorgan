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
package jorgan.io.soundfont.factory;

// jorgan
import jorgan.io.riff.Chunk;
import jorgan.io.riff.RiffFormatException;
import jorgan.io.riff.RiffReader;
import jorgan.io.riff.factory.ChunkFactory;
import jorgan.io.soundfont.Preset;
import jorgan.io.soundfont.PresetHeaderChunk;

/**
 * A factory for {@link jorgan.io.soundfont.PresetHeaderChunk}s.
 */
public class PresetHeaderChunkFactory extends ChunkFactory {

	private static final int PRESET_LENGTH = 38;

	/**
	 * Create a chunk.
	 * 
	 * @param id
	 *            id of chunk
	 * @param dataLength
	 *            length of data contained in the chunk
	 * @return representation of the preset header of a soundfont
	 */
	@Override
	public Chunk createChunk(String id, int dataLength, RiffReader reader)
			throws RiffFormatException {

		PresetHeaderChunk chunk = new PresetHeaderChunk(id, dataLength);

		int i = 0;
		while (true) {
			String name = reader.readString(20);
			short program = reader.readShort();
			short bank = reader.readShort();
			reader.skip(PRESET_LENGTH - (20 + 2 + 2));

			i += PRESET_LENGTH;

			if (i < dataLength) {
				Preset preset = new Preset(name, program, bank);
				chunk.addPreset(preset);
			} else {
				// skip last dummy preset
				break;
			}
		}

		return chunk;
	}
}
