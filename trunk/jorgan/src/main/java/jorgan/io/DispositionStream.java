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
package jorgan.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerException;

import jorgan.disposition.Organ;
import jorgan.io.disposition.ClassMapper;
import jorgan.io.disposition.Conversion;
import jorgan.io.disposition.ElementConverter;
import jorgan.io.disposition.History;
import jorgan.io.disposition.OrganConverter;
import jorgan.io.disposition.ReferenceConverter;
import jorgan.util.IOUtils;
import bias.Configuration;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.io.xml.AbstractXmlDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.thoughtworks.xstream.mapper.MapperWrapper;

/**
 * A {@link jorgan.disposition.Organ} streamer.
 */
public class DispositionStream {

	private static final String ENCODING = "UTF-8";

	private static Configuration config = Configuration.getRoot().get(
			DispositionStream.class);

	private XStream xstream = new XStream(createDriver()) {
		@Override
		protected MapperWrapper wrapMapper(MapperWrapper next) {
			return new ClassMapper(next);
		}
	};

	private int recentMax = 4;

	private List<File> recentFiles = new ArrayList<File>();

	private File recentDirectory;

	private int historySize = 0;

	public DispositionStream() {
		xstream.setMode(XStream.NO_REFERENCES);

		xstream.registerConverter(new OrganConverter(xstream));
		xstream.registerConverter(new ElementConverter(xstream));
		xstream.registerConverter(new ReferenceConverter(xstream));

		config.read(this);
	}

	/**
	 * 
	 * @param file
	 *            the file to read from
	 * @return the read organ
	 * @throws IOException
	 * @throws Exception
	 */
	public Organ read(File file) throws IOException, Exception {
		FileInputStream input = new FileInputStream(file);

		try {
			Organ organ = read(input);
			addRecentFile(file);
			return organ;
		} finally {
			IOUtils.closeQuietly(input);
		}
	}

	public Organ read(InputStream in) throws IOException {
		Organ organ = null;

		try {
			InputStream converted = Conversion.convertAll(in);

			Reader reader = new InputStreamReader(converted, ENCODING);

			organ = (Organ) xstream.fromXML(reader);
		} catch (TransformerException ex) {
			throw new ConversionException(ex);
		}

		return organ;
	}

	public void write(Organ organ, File file) throws IOException {

		File temp = new File(file.getAbsoluteFile().getParentFile(), "."
				+ file.getName());

		FileOutputStream output = new FileOutputStream(temp);
		try {
			write(organ, output);
		} finally {
			IOUtils.closeQuietly(output);
		}

		new History(file).move(historySize);

		temp.renameTo(file);

		addRecentFile(file);
	}

	public void write(Organ organ, OutputStream out) throws IOException {

		Writer writer = new OutputStreamWriter(out, ENCODING);
		writer
				.write("<?xml version=\"1.0\" encoding=\"" + ENCODING
						+ "\" ?>\n");
		xstream.toXML(organ, writer);
	}

	public File getRecentDirectory() {
		return recentDirectory;
	}

	public void setRecentDirectory(File recentDirectory) {
		this.recentDirectory = recentDirectory;
	}

	public File getRecentFile() {
		if (recentFiles.size() > 0) {
			return recentFiles.get(0);
		}
		return null;
	}

	public void setRecentFiles(List<File> recentFiles) {
		this.recentFiles = recentFiles;
	}

	public List<File> getRecentFiles() {
		return recentFiles;
	}

	public int getRecentMax() {
		return recentMax;
	}

	public void setRecentMax(int recentMax) {
		this.recentMax = recentMax;
	}

	private void addRecentFile(File file) {
		try {
			File canonical = file.getCanonicalFile();

			recentFiles.remove(file);
			recentFiles.remove(canonical);

			recentFiles.add(0, canonical);
			if (recentFiles.size() > recentMax) {
				recentFiles.remove(recentMax);
			}

			recentDirectory = canonical.getParentFile();
		} catch (IOException ex) {
			// ignoe
		}

		config.write(this);
	}

	public int getHistorySize() {
		return historySize;
	}

	public void setHistorySize(int historySize) {
		this.historySize = historySize;
	}

	private AbstractXmlDriver createDriver() {
		return new XppDriver(createReplacer());
	}

	private XmlFriendlyReplacer createReplacer() {
		// replaced "$" and "_"
		return new XmlFriendlyReplacer("-", "_");
	}
}