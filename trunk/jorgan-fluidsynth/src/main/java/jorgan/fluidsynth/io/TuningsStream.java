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
package jorgan.fluidsynth.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import jorgan.fluidsynth.disposition.Tuning;
import jorgan.io.xstream.BooleanArrayConverter;
import jorgan.io.xstream.DoubleArrayConverter;
import jorgan.io.xstream.FloatArrayConverter;
import jorgan.io.xstream.IntArrayConverter;
import jorgan.util.IOUtils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XppDriver;

/**
 * A {@link Tuning}s streamer.
 */
public class TuningsStream {

	private static final String ENCODING = "UTF-8";

	private XStream xstream = new XStream(new XppDriver());

	public TuningsStream() {
		xstream.alias("tunings", ArrayList.class);
		xstream.alias("tuning", Tuning.class);

		// primitives
		xstream.registerConverter(new BooleanArrayConverter());
		xstream.registerConverter(new IntArrayConverter());
		xstream.registerConverter(new FloatArrayConverter());
		xstream.registerConverter(new DoubleArrayConverter());
	}

	public List<Tuning> read(File file) throws IOException {
		InputStream input = new FileInputStream(file);

		try {
			return read(input);
		} finally {
			IOUtils.closeQuietly(input);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Tuning> read(InputStream in) throws IOException {
		Reader reader = new InputStreamReader(new BufferedInputStream(in),
				ENCODING);

		try {
			return (List<Tuning>) xstream.fromXML(reader);
		} catch (Exception ex) {
			IOException io = new IOException(ex.getMessage());
			io.initCause(ex);
			throw io;
		}
	}
}