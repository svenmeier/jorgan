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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.TreeSet;
import java.util.Vector;

public class PropertiesCopier {

	private String prefix;

	private Locale locale;

	private String encoding;

	private String comment;

	public PropertiesCopier(String prefix, Locale locale, String encoding,
			String comment) {
		this.prefix = prefix;
		this.locale = locale;
		this.encoding = encoding;
		this.comment = comment;
	}

	public void augment(File file) throws IOException {
		if (!file.exists()) {
			throw new IllegalArgumentException("not exists");
		}

		if (file.isDirectory()) {
			augmentDirectory(file);
		} else {
			augmentFile(file);
		}
	}

	private void augmentDirectory(File directory) throws IOException {
		for (File file : directory.listFiles()) {
			if (file.isDirectory()) {
				augmentDirectory(file);
			} else {
				if (file.getName().equals(prefix + ".properties")) {
					augmentFile(file);
				}
			}
		}
	}

	private void augmentFile(File file) throws IOException {
		Properties properties = new SortedProperties();

		read(file, properties);

		File other = new File(file.getParentFile(), prefix + "_"
				+ locale.toString() + ".properties");
		if (other.exists()) {
			Properties otherProperties = new Properties(properties);

			read(other, otherProperties);

			for (Object key : properties.keySet()) {
				properties.put(key, otherProperties.getProperty((String) key));
			}
		}

		write(other, properties);
	}

	private void read(File file, Properties properties) throws IOException {
		Reader reader = new InputStreamReader(new FileInputStream(file),
				encoding);
		try {
			properties.getClass().getMethod("load",
					new Class[] { Reader.class }).invoke(properties, reader);
		} catch (Exception ex) {
			throw new Error("no java 6");
		} finally {
			reader.close();
		}
	}

	private void write(File other, Properties properties) throws IOException {
		Writer writer = new OutputStreamWriter(new FileOutputStream(other));
		try {
			properties.getClass().getMethod("store",
					new Class[] { Writer.class, String.class }).invoke(
					properties, writer, comment);
		} catch (Exception ex) {
			throw new Error("no java 6");
		} finally {
			writer.close();
		}
	}

	private static class SortedProperties extends Properties {
		@Override
		public synchronized Enumeration<Object> keys() {
			return new Vector<Object>(new TreeSet<Object>(keySet())).elements();
		}
	}

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.append("locale required");
			System.exit(1);
			return;
		}

		new PropertiesCopier("i18n", new Locale(args[0]), "UTF-8",
				"Ensure UTF-8 encoding!").augment(new File("src"));
	}
}
