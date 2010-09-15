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

import jorgan.disposition.Message;
import jorgan.disposition.Organ;
import jorgan.midi.mpl.Command;
import jorgan.midi.mpl.ProcessingException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Converter for {@link Message}s.
 */
public class MessageConverter implements Converter {

	public MessageConverter(XStream xstream) {
		xstream.registerConverter(this);
	}

	@SuppressWarnings("unchecked")
	public boolean canConvert(Class clazz) {
		return Message.class.isAssignableFrom(clazz);
	}

	public void marshal(Object value, HierarchicalStreamWriter writer,
			MarshallingContext context) {

		Message message = (Message) value;

		writer.setValue(Command.format(message.getCommands()));
	}

	/**
	 * @see #initElementsOrgan(Organ)
	 */
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {

		Class<?> type = context.getRequiredType();

		Message message;

		try {
			message = (Message) type.newInstance();
		} catch (Exception ex) {
			throw new ConversionException(ex);
		}

		String string = reader.getValue();

		try {
			message.change(Command.parse(string));
		} catch (ProcessingException ex) {
			throw new ConversionException(ex);
		}

		return message;
	}
}