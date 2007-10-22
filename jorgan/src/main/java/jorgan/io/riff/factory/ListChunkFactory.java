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
package jorgan.io.riff.factory;

// jorgan
import jorgan.io.riff.Chunk;
import jorgan.io.riff.ListChunk;
import jorgan.io.riff.RiffFormatException;
import jorgan.io.riff.RiffReader;

/**
 * A factory for {@link jorgan.io.riff.ListChunk}s.
 */
public class ListChunkFactory extends ChunkFactory {

	@Override
	public ListChunk createChunk(String id, int dataLength, RiffReader reader)
			throws RiffFormatException {
		String type = reader.readId();

		ListChunk chunk = createChunk(id, dataLength, type);

		int index = 4;
		while (index < chunk.getDataLength()) {
			Chunk subChunk = reader.readChunk();
			chunk.addChunk(subChunk);

			index += subChunk.getSize();
		}

		return chunk;
	}

	/**
	 * Hook method for subclasses of this factory that create subclasses of
	 * {@link jorgan.io.riff.ListChunk}s.
	 * 
	 * @param id
	 *            identifier of chunk to create
	 * @param dataLength
	 *            length ot chunk
	 * @param type
	 *            type of list
	 * @return created listChunk
	 */
	protected ListChunk createChunk(String id, int dataLength, String type) {
		return new ListChunk(id, dataLength, type);
	}
}
