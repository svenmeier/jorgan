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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import jorgan.io.riff.factory.ChunkFactory;
import jorgan.io.riff.factory.ListChunkFactory;
import jorgan.io.riff.factory.RiffChunkFactory;

/**
 * A reader for data in <em>Resource Interchange File Format</em>.
 */
public class RiffReader {

	/**
	 * Should values be read in little endian order.
	 */
	private boolean littleEndian = false;

	/**
	 * The factories used to create chunks.
	 */
	private Map<String, ChunkFactory> factories = new HashMap<String, ChunkFactory>();

	/**
	 * The default factory to use for non recognized chunks.
	 */
	private ChunkFactory defaultFactory = new ChunkFactory();

	/**
	 * The inputStream to read from.
	 */
	private InputStream in;

	/**
	 * Create a new reader.
	 * 
	 * @param in
	 *            the reader to read from
	 */
	public RiffReader(InputStream in) {
		this.in = new BufferedInputStream(in);

		factories.put(ListChunk.LIST_ID, new ListChunkFactory());
	}

	/**
	 * Read the contents of the <code>RIFF</code> input.
	 * 
	 * @return the read riff chunk
	 * @throws RiffFormatException
	 */
	public RiffChunk read() throws RiffFormatException {

		try {
			String id = readId();
			if (RiffChunk.RIFF_ID.equals(id)) {
				littleEndian = true;
			} else if (RiffChunk.RIFX_ID.equals(id)) {
				littleEndian = false;
			} else {
				throw new RiffFormatException(
						"riff does not start with RIFF or RIFX");
			}

			int dataLength = readInt();

			ChunkFactory factory = new RiffChunkFactory();

			RiffChunk riffChunk = (RiffChunk) createChunk(factory, id,
					dataLength);

			checkFormat(riffChunk);

			in.close();

			return riffChunk;
		} catch (IOException ex) {
			throw new RiffFormatException(ex);
		}
	}

	/**
	 * Check the format of the riffChunk. <br>
	 * This method should be overriden by subclasses to ensure their custom
	 * <code>RIFF</code> format extensions.
	 * 
	 * @param chunk
	 *            chunk to check format
	 * @throws RiffFormatException
	 */
	protected void checkFormat(RiffChunk chunk) throws RiffFormatException {
	}

	/**
	 * Read a chunk. This method is meant to be called by factories of chunks
	 * that contain other chunks.
	 * 
	 * @return read chunk
	 * @throws RiffFormatException
	 */
	public Chunk readChunk() throws RiffFormatException {

		String id = readId();
		int dataLength = readInt();

		ChunkFactory factory = factories.get(id);
		if (factory == null) {
			factory = defaultFactory;
		}

		return createChunk(factory, id, dataLength);
	}

	/**
	 * Create a chunk.
	 * 
	 * @param factory
	 *            factory to create chunk with
	 * @param id
	 *            id of chunk to create
	 * @param dataLength
	 *            length of data of chunk to create
	 * @return created chunk
	 * @throws RiffFormatException
	 */
	private Chunk createChunk(ChunkFactory factory, String id, int dataLength)
			throws RiffFormatException {
		try {
			Chunk chunk = factory.createChunk(id, dataLength, this);

			if (dataLength % 2 == 1) {
				in.read();
			}
			return chunk;
		} catch (IOException ex) {
			throw new RiffFormatException(ex);
		}
	}

	/**
	 * Skip bytes.
	 * 
	 * @param count
	 *            count of bytes to skip
	 * @throws RiffFormatException
	 */
	public void skip(long count) throws RiffFormatException {
		try {
			for (int i = 0; i < count; i++) {
				in.read();
			}
		} catch (IOException ex) {
			throw new RiffFormatException(ex);
		}
	}

	/**
	 * Read a short.
	 * 
	 * @return read short value
	 * @throws RiffFormatException
	 */
	public short readShort() throws RiffFormatException {
		try {
			if (littleEndian) {
				return (short) (in.read() << 0 | in.read() << 8);
			} else {
				return (short) (in.read() << 8 | in.read() << 0);
			}
		} catch (IOException ex) {
			throw new RiffFormatException(ex);
		}
	}

	/**
	 * Read an integer.
	 * 
	 * @return read integer value
	 * @throws RiffFormatException
	 */
	public int readInt() throws RiffFormatException {
		try {
			if (littleEndian) {
				return in.read() << 0 | in.read() << 8 | in.read() << 16
						| in.read() << 24;
			} else {
				return in.read() << 24 | in.read() << 16 | in.read() << 8
						| in.read() << 0;
			}
		} catch (IOException ex) {
			throw new RiffFormatException(ex);
		}
	}

	/**
	 * Read a chunk id.
	 * 
	 * @return read chunk id
	 * @throws RiffFormatException
	 */
	public String readId() throws RiffFormatException {
		return readString(4);
	}

	/**
	 * Read string.
	 * 
	 * @param length
	 *            length of string to read
	 * @return read string
	 * @throws RiffFormatException
	 */
	public String readString(int length) throws RiffFormatException {
		try {
			byte[] characters = new byte[length];

			for (int c = 0; c < characters.length; c++) {
				characters[c] = (byte) in.read();
			}

			String string = new String(characters);
			int termination = string.indexOf(0);
			if (termination != -1) {
				string = string.substring(0, termination);
			}
			return string;
		} catch (IOException ex) {
			throw new RiffFormatException(ex);
		}
	}

	/**
	 * Register a factory for chunks with the given id.
	 * 
	 * @param id
	 *            id to register factory for
	 * @param factory
	 *            factory to register
	 */
	protected void registerFactory(String id, ChunkFactory factory) {
		factories.put(id, factory);
	}
}
