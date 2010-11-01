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
package jorgan.session;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jorgan.disposition.Organ;
import bias.Configuration;

/**
 * A session of interaction with an {@link Organ}.
 */
public class History {

	private static Configuration config = Configuration.getRoot().get(
			History.class);

	private int max = 4;

	private List<File> files = new ArrayList<File>();

	private File directory;

	public History() {
		config.read(this);
	}

	public File getRecentDirectory() {
		return directory;
	}

	public void setRecentDirectory(File recentDirectory) {
		this.directory = recentDirectory;
	}

	public File getRecentFile() {
		if (files.size() > 0) {
			return files.get(0);
		}
		return null;
	}

	public void setRecentFiles(List<File> recentFiles) {
		this.files = recentFiles;
	}

	public List<File> getRecentFiles() {
		return files;
	}

	public int getRecentMax() {
		return max;
	}

	public void setRecentMax(int recentMax) {
		this.max = recentMax;
	}

	public void addRecentFile(File file) {
		try {
			File canonical = file.getCanonicalFile();

			files.remove(file);
			files.remove(canonical);

			files.add(0, canonical);
			while (files.size() > max) {
				files.remove(files.size() - 1);
			}

			directory = canonical.getParentFile();
		} catch (IOException ignore) {
		}

		config.write(this);
	}
}