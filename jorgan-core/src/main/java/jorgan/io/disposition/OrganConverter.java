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

import jorgan.disposition.Element;
import jorgan.disposition.Organ;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Converter for {@link Organ}s that adds version information an marshalling and
 * sets parent reference on unmarshalling.
 * 
 * @see #marshallVersion(HierarchicalStreamWriter)
 * @see #initElementsOrgan(Organ)
 */
public class OrganConverter implements Converter {

	private Converter nested;

	public OrganConverter(XStream xstream) {
		xstream.registerConverter(this);

		xstream.useAttributeFor(Organ.class, "version");
		xstream.omitField(Element.class, "organ");

		nested = xstream.getConverterLookup().lookupConverterForType(
				Object.class);
	}

	@SuppressWarnings("rawtypes")
	public boolean canConvert(Class clazz) {
		return Organ.class.isAssignableFrom(clazz);
	}

	/**
	 * @see #marshallVersion(HierarchicalStreamWriter)
	 */
	public void marshal(Object value, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		Organ organ = (Organ) value;

		nested.marshal(organ, writer, context);
	}

	/**
	 * @see #initElementsOrgan(Organ)
	 */
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {

		Organ organ = (Organ) nested.unmarshal(reader, context);

		for (Element element : organ.getElements()) {
			organ.bind(element);
		}

		context.put(Organ.class, organ);

		return organ;
	}
}