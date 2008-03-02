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
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;

/**
 * Collection of utility methods for classes.
 */
public class ClassUtils {

	private static final String JAR_PROTOCOL = "jar:";

	private static final String FILE_PROTOCOL = "file:";

	/**
	 * Analyse where the given class was loaded from, i.e.
	 * <ul>
	 * <li>the directory of the jar-file, the class is contained in, or</li>
	 * <lI>the base directory of the class-file hierarchy</li>
	 * </ul>
	 * 
	 * @param clazz
	 * @return
	 */
	public static File getDirectory(Class<?> clazz) {
		try {
			URL url = getClassURL(clazz);

			if (url.toString().startsWith(JAR_PROTOCOL)) {
				// jar:file:/C:/FooMatic/./lib/foo.jar!/foo/Bar.class
				JarURLConnection jarCon = (JarURLConnection) url
						.openConnection();

				// file:/C:/FooMatic/./lib/foo.jar
				URL jarUrl = jarCon.getJarFileURL();

				// /C:/FooMatic/./lib/foo.jar
				File jarFile = new File(URLDecoder.decode(jarUrl.getPath(),
						"UTF-8"));

				// /C:/FooMatic/./lib
				return jarFile.getParentFile();
			} else if (url.toString().startsWith(FILE_PROTOCOL)) {
				String path = url.getPath();

				return new File(path.substring(FILE_PROTOCOL.length(), path
						.length()
						- getClassResourceName(clazz).length()));
			}
		} catch (Exception fallBackToUserDir) {
		}

		return new File(System.getProperty("user.dir"));
	}

	/**
	 * Get URL of the given class.
	 * 
	 * @param clazz
	 *            class to get URL for
	 * @return the URL this class was loaded from
	 */
	private static URL getClassURL(Class<?> clazz) {
		return clazz.getResource(getClassResourceName(clazz));
	}

	private static String getClassResourceName(Class<?> clazz) {
		return ("/" + clazz.getName().replace('.', '/') + ".class");
	}

	/**
	 * Load a library with given name from the given path.
	 * 
	 * @param path
	 * @param name
	 */
	public static void loadLibrary(File path, String name) {
		String library;
		try {
			library = new File(path, System.mapLibraryName(name))
					.getCanonicalPath();
		} catch (IOException e) {
			throw new UnsatisfiedLinkError(e.getMessage());
		}
		System.load(library);
	}
}