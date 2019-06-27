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

import jorgan.midi.mpl.Command;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Converter for {@link Commands}s.
 */
public class CommandConverter implements Converter {

	public CommandConverter(XStream xstream) {
		xstream.registerConverter(this);
	}

	@SuppressWarnings("rawtypes")
	public boolean canConvert(Class clazz) {
		return Command.class.isAssignableFrom(clazz);
	}

	public void marshal(Object value, HierarchicalStreamWriter writer,
			MarshallingContext context) {

		Command command = (Command) value;

		writer.setValue(command.toString());
	}

	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {

		try {
			return Command.fromString(reader.getValue());
		} catch (Exception ex) {
			throw new ConversionException(ex);
		}
	}
}