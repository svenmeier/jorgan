package jorgan.bootstrap;

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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import jorgan.util.ClassUtils;

public class Classpath {

	public Classpath(String path) throws Exception {
		URL[] urls = getURLs(path);

		Thread.currentThread().setContextClassLoader(new URLClassLoader(urls));
	}

	private URL[] getURLs(String path) throws MalformedURLException {

		List<URL> urls = new ArrayList<URL>();

		File directory = ClassUtils.getDirectory(getClass());
		File file = new File(directory, path);
		if (file.exists()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				urls.add(files[i].toURI().toURL());
			}
		}

		return urls.toArray(new URL[urls.size()]);
	}
}
