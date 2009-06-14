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
	public static final String FILE_SUFFIX = ".mid";

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
	
	  /**
	   * Get the name of the disposition file without suffix.
	   *
	   * @param file  file to get name for
	   * @return      name of file
	   * @see         #FILE_SUFFIX
	   */
	  public static String removeSuffix(File file) {
	    String name = file.getName();
	    if (name.endsWith(FILE_SUFFIX)) {
	      name = name.substring(0, name.indexOf(FILE_SUFFIX));
	    }
	    return name;
	  }

	  /**
	   * Add the suffix to the given file.
	   *
	   * @param file    file to add suffix for
	   * @return        resulting file
	   */
	  public static File addSuffix(File file) {
	    String name = file.getName();
	    if (!name.endsWith(FILE_SUFFIX)) {
	      file = new File(file.getParentFile(), name + FILE_SUFFIX);
	    }
	    return file;
	  }
}