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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jorgan.io.skin.ColorConverter;
import jorgan.io.skin.FontConverter;
import jorgan.skin.ButtonLayer;
import jorgan.skin.CompositeLayer;
import jorgan.skin.ImageLayer;
import jorgan.skin.Skin;
import jorgan.skin.SliderLayer;
import jorgan.skin.Style;
import jorgan.skin.TextLayer;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.KXml2Driver;

/**
 * A {@link jorgan.skin.Skin} streamer.
 */
public class SkinStream {

	private XStream xstream = new XStream(new KXml2Driver());

	public SkinStream() {
		xstream.alias("skin", Skin.class);
		xstream.alias("style", Style.class);
		xstream.alias("image", ImageLayer.class);
		xstream.alias("text", TextLayer.class);
		xstream.alias("composite", CompositeLayer.class);
		xstream.alias("button", ButtonLayer.class);
		xstream.alias("slider", SliderLayer.class);

		xstream.registerConverter(new ColorConverter());
		xstream.registerConverter(new FontConverter());
	}

	/**
	 * @param file
	 *            the file to read from
	 * @return the read skin
	 * @throws IOException
	 * @throws Exception
	 */
	public Skin read(InputStream in) throws IOException {
		try {
			return (Skin) xstream.fromXML(new BufferedInputStream(in));
		} catch (Exception ex) {
			IOException io = new IOException(ex.getMessage());
			io.initCause(ex);
			throw io;
		}
	}

	public void write(Skin skin, OutputStream out) throws IOException {
		xstream.toXML(skin, new BufferedOutputStream(out));

		out.close();
	}
}