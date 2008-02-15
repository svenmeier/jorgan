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
package jorgan.io.disposition;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class BooleanArrayConverter implements Converter {

	private boolean[] tmp = new boolean[0];

	@SuppressWarnings("unchecked")
	public boolean canConvert(Class clazz) {
		return tmp.getClass() == clazz;
	}

	public void marshal(Object value, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		boolean[] bs = (boolean[]) value;

		StringBuffer buffer = new StringBuffer(bs.length);
		for (boolean b : bs) {
			buffer.append(b ? "1" : "0");
		}

		writer.setValue(buffer.toString());
	}

	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {

		String value = reader.getValue();

		boolean[] bs = new boolean[value.length()];
		for (int b = 0; b < bs.length; b++) {
			bs[b] = value.charAt(b) == '1' ? true : false;
		}
		return bs;
	}
}