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
package jorgan.io.skin;

import java.awt.Font;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class FontConverter implements Converter {

	@SuppressWarnings("rawtypes")
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