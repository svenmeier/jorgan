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
package jorgan.io.riff;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a listChunk.
 */
public class ListChunk extends Chunk {

	public static final String LIST_ID = "LIST";

	/**
	 * The type of this listChunk.
	 */
	private String type;

	/**
	 * The sub chunks.
	 */
	private List<Chunk> subChunks = new ArrayList<Chunk>();

	/**
	 * Create a list chunk.
	 * 
	 * @param id
	 *            id of list chunk, must be <code>LIST</code>
	 * @param dataLength
	 *            length of data of chunk
	 * @param type
	 *            type
	 */
	public ListChunk(String id, int dataLength, String type) {
		super(id, dataLength);

		this.type = type;
	}

	/**
	 * Get the type of this chunk.
	 * 
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Add a chunk.
	 * 
	 * @param chunk
	 *            chunk to add
	 */
	public void addChunk(Chunk chunk) {
		subChunks.add(chunk);
	}

	/**
	 * Get the chunk with the given id.
	 * 
	 * @param id
	 *            id to get chunk for
	 * @return chunk or <code>null</code> if no matching chunk is contained
	 */
	public Chunk getChunk(String id) {
		for (int c = 0; c < subChunks.size(); c++) {
			Chunk chunk = subChunks.get(c);
			if (id.equals(chunk.getId())) {
				return chunk;
			}
		}
		return null;
	}

	/**
	 * Get the listChunk with the given type.
	 * 
	 * @param type
	 *            type to get chunk for
	 * @return listChunk or <code>null</code> if no mathing chunk is contained
	 */
	public ListChunk getListChunk(String type) {
		for (int c = 0; c < subChunks.size(); c++) {
			Chunk chunk = subChunks.get(c);
			if (chunk instanceof ListChunk) {
				ListChunk listChunk = (ListChunk) chunk;
				if (type.equals(listChunk.getType())) {
					return listChunk;
				}
			}
		}
		return null;
	}
}
