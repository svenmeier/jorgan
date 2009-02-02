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
package jorgan.skin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import jorgan.io.SkinStream;
import jorgan.util.IOUtils;

/**
 * Manager of skins.
 */
public class SkinManager implements ISkinManager {

	private static final String SKIN_FILE = "skin.xml";

	private static final String ZIP_SUFFIX = ".zip";

	private static SkinManager instance;

	private Map<File, Skin> skins = new HashMap<File, Skin>();

	public Skin getSkin(File file) throws IOException {
		if (file == null) {
			throw new IllegalArgumentException("file must not be null");
		}
		file = file.getAbsoluteFile();

		Skin skin = skins.get(file);
		if (skin == null) {
			skin = loadSkin(file);

			skins.put(file, skin);
		}

		return skin;
	}

	private Skin loadSkin(File file) throws IOException {
		Skin skin = null;

		SkinSource source = createSkinDirectory(file);
		if (source == null) {
			source = createSkinZip(file);
		}

		if (source != null) {
			InputStream input = source.getURL(SKIN_FILE).openStream();
			try {
				skin = new SkinStream().read(input);
				skin.setSource(source);
			} catch (Exception ex) {
				IOException io = new IOException(ex.getMessage());
				io.initCause(ex);
				throw io;
			} finally {
				IOUtils.closeQuietly(input);
			}
		}

		return skin;
	}

	private SkinSource createSkinDirectory(File file) {

		if (file.isDirectory()) {
			return new SkinDirectory(file);
		}
		return null;
	}

	private SkinSource createSkinZip(File file) {

		if (file.getName().endsWith(ZIP_SUFFIX)) {
			return new SkinZip(file);
		}
		return null;
	}

	/**
	 * A source of a skin contained in a directory.
	 */
	private class SkinDirectory implements SkinSource {

		private File directory;

		private SkinDirectory(File directory) {
			this.directory = directory;
		}

		public URL getURL(String name) {
			try {
				return new File(directory, name).toURI().toURL();
			} catch (MalformedURLException ex) {
				return null;
			}
		}
	}

	/**
	 * A source of a skin contained in a zipFile.
	 */
	private class SkinZip implements SkinSource {

		private File file;

		private SkinZip(File file) {
			this.file = file;
		}

		public URL getURL(String name) {
			try {
				return new URL("jar:" + file.toURI().toURL() + "!/" + name);
			} catch (MalformedURLException ex) {
				return null;
			}
		}
	}

	/**
	 * Get the singleton instance.
	 * 
	 * @return manager of {@Skin}s.
	 */
	public static SkinManager instance() {
		if (instance == null) {
			instance = new SkinManager();
		}

		return instance;
	}
}