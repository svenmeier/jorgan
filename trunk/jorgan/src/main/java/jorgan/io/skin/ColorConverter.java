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

import java.awt.Color;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class ColorConverter implements Converter {

	@SuppressWarnings("unchecked")
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

		int red;
		int green;
		int blue;

		String value = reader.getValue();
		if (value == null) {
			reader.moveDown();
			red = Integer.parseInt(reader.getValue());
			reader.moveUp();

			reader.moveDown();
			green = Integer.parseInt(reader.getValue());
			reader.moveUp();

			reader.moveDown();
			blue = Integer.parseInt(reader.getValue());
			reader.moveUp();
		} else {
			red = Integer.parseInt(value.substring(0, 2), 16);
			green = Integer.parseInt(value.substring(2, 4), 16);
			blue = Integer.parseInt(value.substring(4, 6), 16);
		}

		return new Color(red, green, blue);
	}
}