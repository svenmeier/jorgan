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

import java.lang.reflect.Field;

import jorgan.Info;
import jorgan.disposition.Element;
import jorgan.disposition.Organ;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Converter for {@link Organ}s that adds version information an marshalling
 * and sets parent reference on unmarshalling.
 * 
 * @see #marshallVersion(HierarchicalStreamWriter)
 * @see #unmarshallElementsOrgan(Organ)
 */
public class OrganConverter implements Converter {

	private static Field elementOrganField;

	private Converter nested;

	public OrganConverter(XStream xstream) {
		xstream.registerConverter(this);

		xstream.omitField(Element.class, "organ");

		nested = xstream.getConverterLookup().lookupConverterForType(
				Object.class);
	}

	@SuppressWarnings("unchecked")
	public boolean canConvert(Class clazz) {
		return Organ.class.isAssignableFrom(clazz);
	}

	/**
	 * @see #marshallVersion(HierarchicalStreamWriter)
	 */
	public void marshal(Object value, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		Organ organ = (Organ) value;

		marshallVersion(writer);

		nested.marshal(organ, writer, context);
	}

	/**
	 * Write version information to the organ element.
	 * 
	 * @param writer
	 *            writer
	 */
	protected void marshallVersion(HierarchicalStreamWriter writer) {
		writer.addAttribute("version", new Info().getVersion());
	}

	/**
	 * @see #unmarshallElementsOrgan(Organ)
	 */
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {

		Organ organ = (Organ) nested.unmarshal(reader, context);

		unmarshallElementsOrgan(organ);

		return organ;
	}

	/**
	 * Set the parent reference in all elements of the given organ.
	 * 
	 * @param organ
	 *            organ
	 */
	protected void unmarshallElementsOrgan(Organ organ) {
		try {
			if (elementOrganField == null) {
				elementOrganField = Element.class.getDeclaredField("organ");
				elementOrganField.setAccessible(true);
			}

			for (Element element : organ.getElements()) {
				elementOrganField.set(element, organ);
			}
		} catch (Exception ex) {
			throw new ConversionException(ex);
		}
	}
}