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

import jorgan.App;
import jorgan.disposition.Activator;
import jorgan.disposition.Captor;
import jorgan.disposition.Combination;
import jorgan.disposition.Console;
import jorgan.disposition.Coupler;
import jorgan.disposition.Incrementer;
import jorgan.disposition.Keyboard;
import jorgan.disposition.Keyer;
import jorgan.disposition.Label;
import jorgan.disposition.Memory;
import jorgan.disposition.Organ;
import jorgan.disposition.Reference;
import jorgan.disposition.Regulator;
import jorgan.disposition.Sequence;
import jorgan.disposition.SoundSource;
import jorgan.disposition.Stop;
import jorgan.disposition.Swell;
import jorgan.disposition.Tremulant;
import jorgan.disposition.Variation;
import jorgan.disposition.Combination.CombinationReference;
import jorgan.disposition.Console.ConsoleReference;
import jorgan.io.disposition.BooleanArrayConverter;
import jorgan.io.disposition.Conversion;
import jorgan.io.disposition.ElementConverter;
import jorgan.io.disposition.History;
import jorgan.io.disposition.KeyConverter;
import jorgan.io.disposition.OrganConverter;
import jorgan.io.disposition.ReferenceConverter;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * A {@link jorgan.disposition.Organ} streamer.
 */
public class DispositionStream {

	private XStream xstream = new XStream(new DomDriver());

	private int recentMax = 4;

	private List<File> recentFiles = new ArrayList<File>();

	private File recentDirectory;

	private int historySize = 0;

	public DispositionStream() {
		xstream.setMode(XStream.NO_REFERENCES);

		xstream.alias("organ", Organ.class);
		xstream.alias("console", Console.class);
		xstream.alias("consoleReference", ConsoleReference.class);
		xstream.alias("label", Label.class);
		xstream.alias("keyboard", Keyboard.class);
		xstream.alias("soundSource", SoundSource.class);
		xstream.alias("stop", Stop.class);
		xstream.alias("coupler", Coupler.class);
		xstream.alias("combination", Combination.class);
		xstream.alias("combinationReference", CombinationReference.class);
		xstream.alias("captor", Captor.class);
		xstream.alias("swell", Swell.class);
		xstream.alias("tremulant", Tremulant.class);
		xstream.alias("variation", Variation.class);
		xstream.alias("sequence", Sequence.class);
		xstream.alias("activator", Activator.class);
		xstream.alias("regulator", Regulator.class);
		xstream.alias("keyer", Keyer.class);
		xstream.alias("memory", Memory.class);
		xstream.alias("incrementer", Incrementer.class);
		xstream.alias("reference", Reference.class);

		xstream.registerConverter(new OrganConverter(xstream));
		xstream.registerConverter(new ElementConverter(xstream));
		xstream.registerConverter(new ReferenceConverter(xstream));
		xstream.registerConverter(new KeyConverter());
		xstream.registerConverter(new BooleanArrayConverter());

		App.getBias().getValues(this);
	}

	public Organ read(File file) throws IOException {
		Organ organ = read(new FileInputStream(file));

		addRecentFile(file);

		return organ;
	}

	public Organ read(InputStream in) throws IOException {
		Organ organ = null;

		try {
			InputStream converted = Conversion.convertAll(in);

			Reader reader = new InputStreamReader(converted);

			organ = (Organ) xstream.fromXML(reader);
		} catch (TransformerException ex) {
			throw new ConversionException(ex);
		} finally {
			try {
				in.close();
			} catch (IOException ignore) {
			}
		}

		return organ;
	}

	public void write(Organ organ, File file) throws IOException {
		new History(file, historySize).add();

		write(organ, new FileOutputStream(file));

		addRecentFile(file);
	}

	public void write(Organ organ, OutputStream out) throws IOException {

		try {
			Writer writer = new OutputStreamWriter(out, "UTF-8");
			writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
			xstream.toXML(organ, writer);
		} finally {
			try {
				out.close();
			} catch (IOException ignored) {
			}
		}
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

		App.getBias().setValues(this);
	}

	public int getHistorySize() {
		return historySize;
	}

	public void setHistorySize(int historySize) {
		this.historySize = historySize;
	}
}