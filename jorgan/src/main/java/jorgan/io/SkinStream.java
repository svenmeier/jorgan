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

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jorgan.skin.ButtonLayer;
import jorgan.skin.CompositeLayer;
import jorgan.skin.ImageLayer;
import jorgan.skin.Skin;
import jorgan.skin.SliderLayer;
import jorgan.skin.Style;
import jorgan.skin.TextLayer;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * A {@link jorgan.skin.Skin} streamer.
 */
public class SkinStream {

	private XStream xstream = new XStream(new DomDriver());

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
	 * @param file	the file to read from
	 * @return	the read skin
	 * @throws IOException
	 * @throws Exception
	 */
	public Skin read(InputStream in) throws IOException, Exception {
		try {
			Skin skin = (Skin) xstream.fromXML(in);
			return skin;
		} finally {
			in.close();			
		}
	}

	public void write(Skin skin, OutputStream out) throws IOException {
		xstream.toXML(skin, out);

		out.close();
	}

	private class FontConverter implements Converter {

		public boolean canConvert(Class clazz) {
			return clazz.equals(Font.class);
		}

		public void marshal(Object value, HierarchicalStreamWriter writer,
				MarshallingContext context) {
			Font font = (Font) value;

			writer.startNode("name");
			writer.setValue("" + font.getName());
			writer.endNode();

			writer.startNode("style");
			writer.setValue("" + font.getStyle());
			writer.endNode();

			writer.startNode("size");
			writer.setValue("" + font.getSize());
			writer.endNode();
		}

		public Object unmarshal(HierarchicalStreamReader reader,
				UnmarshallingContext context) {

			reader.moveDown();
			String name = reader.getValue();
			reader.moveUp();

			reader.moveDown();
			int style = Integer.parseInt(reader.getValue());
			reader.moveUp();

			reader.moveDown();
			int size = Integer.parseInt(reader.getValue());
			reader.moveUp();

			return new Font(name, style, size);
		}
	}

	private class ColorConverter implements Converter {

		public boolean canConvert(Class clazz) {
			return clazz.equals(Color.class);
		}

		public void marshal(Object value, HierarchicalStreamWriter writer,
				MarshallingContext context) {
			Color color = (Color) value;

			writer.startNode("red");
			writer.setValue("" + color.getRed());
			writer.endNode();

			writer.startNode("green");
			writer.setValue("" + color.getGreen());
			writer.endNode();

			writer.startNode("blue");
			writer.setValue("" + color.getBlue());
			writer.endNode();
		}

		public Object unmarshal(HierarchicalStreamReader reader,
				UnmarshallingContext context) {

			reader.moveDown();
			int red = Integer.parseInt(reader.getValue());
			reader.moveUp();

			reader.moveDown();
			int green = Integer.parseInt(reader.getValue());
			reader.moveUp();

			reader.moveDown();
			int blue = Integer.parseInt(reader.getValue());
			reader.moveUp();

			return new Color(red, green, blue);
		}
	}
}