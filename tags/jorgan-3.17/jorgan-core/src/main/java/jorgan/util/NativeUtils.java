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
	 * Load a named library from a directory.<br>
	 * Note: Loading of a JNI library should always be done in the corresponding
	 * Java class or otherwise native methods may result in
	 * {@link UnsatisfiedLinkError}s if different {@link ClassLoader}s are
	 * involved.
	 * 
	 * @param directory
	 *            directory the library is located in
	 * @param name
	 *            name of library
	 */
	public static void load(File directory, String name)
			throws UnsatisfiedLinkError {
		load(new File(directory, System.mapLibraryName(name)));
	}

	/**
	 * Load a library from a file.
	 * 
	 * @param file
	 *            the library file
	 */
	public static void load(File file) throws UnsatisfiedLinkError {
		try {
			System.load(file.getCanonicalPath());
		} catch (IOException ex) {
			UnsatisfiedLinkError error = new UnsatisfiedLinkError();
			error.initCause(ex);
			throw error;
		}
	}

	public static boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().contains("win");
	}

	public static boolean isMac() {
		return System.getProperty("os.name").toLowerCase().contains("mac");
	}
}