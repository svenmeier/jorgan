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
package jorgan.io.xstream;

import java.util.StringTokenizer;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class IntArrayConverter implements Converter {

	private Class<?> clazz = new int[0].getClass();

	@SuppressWarnings("rawtypes")
	public boolean canConvert(Class clazz) {
		return this.clazz == clazz;
	}

	public void marshal(Object value, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		int[] is = (int[]) value;

		StringBuffer buffer = new StringBuffer(is.length * 2);
		for (int i : is) {
			if (buffer.length() > 0) {
				buffer.append(",");
			}
			buffer.append(i);
		}

		writer.setValue(buffer.toString());
	}

	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {

		StringTokenizer tokens = new StringTokenizer(reader.getValue(), ",");
		int[] is = new int[tokens.countTokens()];
		for (int i = 0; i < is.length; i++) {
			is[i] = Integer.parseInt(tokens.nextToken().trim());
		}
		return is;
	}
}