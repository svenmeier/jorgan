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

import jorgan.io.riff.Chunk;
import jorgan.io.riff.RiffFormatException;
import jorgan.io.riff.RiffReader;

/**
 * A factory of chunks.
 */
public class ChunkFactory {

	/**
	 * Create a chunk representation for the chunk with the given id and data
	 * length. This default implementation skips the contained data. <br>
	 * Subclasses should override this method and use the given reader to read
	 * the data according to their needs - they must however ensure that the
	 * complete length of data is read or skipped.
	 * 
	 * @param id
	 *            id of chunk to create
	 * @param dataLength
	 *            length of data of chunk to create
	 * @param reader
	 *            the containing RiffReader
	 * @return created Chunk
	 * @throws RiffFormatException
	 */
	public Chunk createChunk(String id, int dataLength, RiffReader reader)
			throws RiffFormatException {
		Chunk chunk = new Chunk(id, dataLength);

		reader.skip(chunk.getDataLength());

		return chunk;
	}
}
