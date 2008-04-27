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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jorgan.io.SkinStream;
import jorgan.util.Bootstrap;
import jorgan.util.ClassUtils;
import jorgan.util.IOUtils;

/**
 * Manager of skins.
 */
public class SkinManager implements ISkinManager {

	private static final Logger logger = Logger.getLogger(SkinManager.class
			.getName());

	/**
	 * The name of the system property to specify the path to load skins from.
	 * <br>
	 * If this system property is not set, skins will be loaded in a "skins"
	 * folder relative to the installation directory.
	 */
	private static final String SKINS_PATH_PROPERTY = "jorgan.skins.path";

	private static final String SKIN_FILE = "skin.xml";

	private static final String ZIP_SUFFIX = ".zip";

	private static SkinManager instance;

	private List<Skin> skins = new ArrayList<Skin>();

	private void initSkins() {
		File dir = new File(System.getProperty(SKINS_PATH_PROPERTY, ClassUtils
				.getDirectory(Bootstrap.class)
				+ "/skins"));
		if (dir.exists()) {
			for (String entry : dir.list()) {
				try {
					initSkin(new File(dir, entry));
				} catch (Exception ex) {
					logger.log(Level.INFO, "ignoring skin '" + entry + "'", ex);
				}
			}
		}
	}

	private void initSkin(File file) throws IOException {
		SkinSource source = createSkinDirectory(file);
		if (source == null) {
			source = createSkinZip(file);
		}

		if (source != null) {
			InputStream input = source.getURL(SKIN_FILE).openStream();
			try {
				Skin skin = new SkinStream().read(input);
				skin.setSource(source);
				skins.add(skin);
			} finally {
				IOUtils.closeQuietly(input);
			}
		}
	}

	private SkinSource createSkinDirectory(File file) throws IOException {

		if (file.isDirectory()) {
			return new SkinDirectory(file);
		}
		return null;
	}

	private SkinSource createSkinZip(File file) throws IOException {

		if (file.getName().endsWith(ZIP_SUFFIX)) {
			return new SkinZip(file);
		}
		return null;
	}

	public String[] getSkinNames() {

		String[] names = new String[1 + skins.size()];

		for (int s = 0; s < skins.size(); s++) {
			Skin skin = skins.get(s);
			names[s + 1] = skin.getName();
		}

		return names;
	}

	public Skin getSkin(String skinName) {
		for (int s = 0; s < skins.size(); s++) {
			Skin skin = skins.get(s);

			if (skin.getName().equals(skinName)) {
				return skin;
			}
		}

		return null;
	}

	public String[] getStyleNames(String skinName) {

		Skin skin = getSkin(skinName);
		if (skin == null) {
			return new String[0];
		} else {
			return skin.getStyleNames();
		}
	}

	/**
	 * A source of a skin contained in a directory.
	 */
	private class SkinDirectory implements SkinSource {

		private File directory;

		private SkinDirectory(File directory) throws IOException {
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

		private SkinZip(File file) throws IOException {
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

			instance.initSkins();
		}

		return instance;
	}
}