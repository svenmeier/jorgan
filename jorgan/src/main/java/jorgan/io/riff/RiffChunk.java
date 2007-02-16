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

/**
 * Representation of the main chunk of a <code>RIFF</code> file.
 */
public class RiffChunk extends ListChunk {

	public static final String RIFF_ID = "RIFF";

	public static final String RIFX_ID = "RIFX";

	/**
	 * Create a new riffChunk.
	 * 
	 * @param id
	 *            id must be <code>RIFF</code> or <code>RIFX</code>
	 * @param dataLength
	 *            length of data
	 * @param type
	 *            type
	 */
	public RiffChunk(String id, int dataLength, String type) {
		super(id, dataLength, type);
	}
}
