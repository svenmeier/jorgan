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
package jorgan.recorder.io;

import java.io.File;
import java.io.FileFilter;

/**
 * Filter for Midi files.
 */
public class MidiFileFilter implements FileFilter {

	/**
	 * The file suffix of midi files.
	 */
	public static final String FILE_SUFFIX = ".midi";

	/**
	 * @see java.io.FileFilter#accept(File)
	 * 
	 * @param file
	 *            file to accept
	 * @return <code>true</code> for directories and dispositions
	 */
	public boolean accept(File file) {
		return file.isDirectory() || file.getName().endsWith(FILE_SUFFIX);
	}
}