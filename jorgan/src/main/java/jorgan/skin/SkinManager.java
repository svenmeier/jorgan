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

import jorgan.disposition.Console;
import jorgan.io.SkinStream;
import jorgan.problem.ElementProblems;
import jorgan.problem.Problem;
import jorgan.problem.Severity;
import jorgan.util.IOUtils;
import bias.Configuration;
import bias.util.MessageBuilder;

/**
 * Manager of skins.
 */
public abstract class SkinManager {

	private static Configuration config = Configuration.getRoot().get(
			SkinManager.class);

	private static final String SKIN_FILE = "skin.xml";

	private static final String ZIP_SUFFIX = ".zip";

	private Map<String, Skin> skins = new HashMap<String, Skin>();

	private ElementProblems problems;

	public SkinManager(ElementProblems problems) {
		this.problems = problems;
	}

	public Skin getSkin(Console console) {
		if (console == null) {
			throw new IllegalArgumentException("console must not be null");
		}

		problems.removeProblem(new Problem(Severity.ERROR, console, "skin",
				null));

		if (console.getSkin() == null) {
			return null;
		}

		Skin skin = skins.get(console.getSkin());
		if (skin != null) {
			return skin;
		}

		try {
			skin = loadSkin(resolve(console.getSkin()));
			skins.put(console.getSkin(), skin);
		} catch (IOException e) {
			problems.addProblem(new Problem(Severity.ERROR, console, "skin",
					createMessage("skinLoad", skin)));
		}

		return skin;
	}

	protected abstract File resolve(String skin) throws IOException;

	protected String createMessage(String key, Object... args) {
		MessageBuilder builder = new MessageBuilder();

		return config.get(key).read(builder).build(args);
	}

	private Skin loadSkin(File file) throws IOException {
		Skin skin = null;

		Resolver resolver = createSkinDirectory(file);
		if (resolver == null) {
			resolver = createSkinZip(file);
			if (resolver == null) {
				throw new IOException();
			}
		}

		if (resolver != null) {
			InputStream input = resolver.resolve(SKIN_FILE).openStream();
			try {
				skin = new SkinStream().read(input);
				skin.setResolver(resolver);
			} finally {
				IOUtils.closeQuietly(input);
			}
		}

		return skin;
	}

	private Resolver createSkinDirectory(File file) {

		if (file.isDirectory()) {
			return new SkinDirectory(file);
		}
		return null;
	}

	private Resolver createSkinZip(File file) {

		if (file.getName().endsWith(ZIP_SUFFIX)) {
			return new SkinZip(file);
		}
		return null;
	}

	/**
	 * A source of a skin contained in a directory.
	 */
	private class SkinDirectory implements Resolver{

		private File directory;

		private SkinDirectory(File directory) {
			this.directory = directory;
		}

		public URL resolve(String name) {
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
	private class SkinZip implements Resolver {

		private File file;

		private SkinZip(File file) {
			this.file = file;
		}

		public URL resolve(String name) {
			try {
				return new URL("jar:" + file.toURI().toURL() + "!/" + name);
			} catch (MalformedURLException ex) {
				return null;
			}
		}
	}
}