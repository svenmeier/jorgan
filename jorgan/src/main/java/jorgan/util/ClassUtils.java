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
import java.net.URL;

/**
 * Collection of utility methods for classes.
 */
public class ClassUtils {

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
			URL url = clazz.getProtectionDomain().getCodeSource().getLocation();

			File file = new File(url.getPath());
			if (!file.isDirectory()) {
				// jar parent directory
				file = file.getParentFile();
			}
			return file;
		} catch (Exception fallBackToUserDir) {
			return new File(System.getProperty("user.dir"));
		}
	}
}