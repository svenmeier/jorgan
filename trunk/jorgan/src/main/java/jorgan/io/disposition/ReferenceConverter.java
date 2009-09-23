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

import jorgan.disposition.Element;
import jorgan.disposition.Organ;
import jorgan.disposition.Reference;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.DataHolder;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Converter for {@link Reference}s.
 * 
 * @see OrganConverter
 * @see DataHolder#get(Object)
 */
public class ReferenceConverter implements Converter {

	private static Field referenceElementField;

	static {
		try {
			referenceElementField = Reference.class.getDeclaredField("element");
			referenceElementField.setAccessible(true);
		} catch (Exception ex) {
			throw new Error(ex);
		}
	}

	private Converter nested;

	public ReferenceConverter(XStream xstream) {
		xstream.registerConverter(this);

		xstream.omitField(Reference.class, "element");

		nested = xstream.getConverterLookup().lookupConverterForType(
				Object.class);
	}

	@SuppressWarnings("unchecked")
	public boolean canConvert(Class clazz) {
		return Reference.class.isAssignableFrom(clazz);
	}

	/**
	 * @see #marshallVersion(HierarchicalStreamWriter)
	 */
	public void marshal(Object value, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		Reference<?> reference = (Reference<?>) value;

		writer.addAttribute("id", "" + reference.getElement().getId());

		nested.marshal(reference, writer, context);
	}

	/**
	 * @see #unmarshallElementsOrgan(Organ)
	 */
	public Object unmarshal(HierarchicalStreamReader reader,
			final UnmarshallingContext context) {

		final Long id = Long.valueOf(reader.getAttribute("id"));

		final Reference<?> reference = (Reference<?>) nested.unmarshal(reader,
				context);

		context.addCompletionCallback(new Runnable() {
			public void run() {
				Organ organ = (Organ) context.get(Organ.class);

				Element element = organ.getElement(id);

				try {
					referenceElementField.set(reference, element);
				} catch (Exception ex) {
					throw new Error(ex);
				}
			}
		}, 0);

		return reference;
	}
}