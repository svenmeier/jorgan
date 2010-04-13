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
package jorgan.util;

import java.io.File;
import java.io.IOException;

/**
 * Collection of utility methods for native code.
 */
public class NativeUtils {

	/**
	 * Get the name of a library.<br>
	 * Note: Loading of a JNI library should always be done in the corresponding
	 * Java class or otherwise native methods may result in
	 * {@link UnsatisfiedLinkError}s if different {@link ClassLoader}s are
	 * involved.
	 * 
	 * @param path
	 *            path of library
	 * @param name
	 *            name of library
	 */
	public static String getLibraryName(File path, String name) {
		String library;
		try {
			library = new File(path, System.mapLibraryName(name))
					.getCanonicalPath();
		} catch (IOException e) {
			throw new Error(e.getMessage());
		}
		return library;
	}
}