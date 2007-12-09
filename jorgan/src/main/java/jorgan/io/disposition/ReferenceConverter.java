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
import jorgan.disposition.Reference;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Converter for {@link Reference}s that writes their element as an attribute
 * on marshalling and links the element on unmarshalling in a completion
 * callback.
 * 
 * @see #marshalElement(Reference, HierarchicalStreamWriter, MarshallingContext)
 * @see UnmarshallingContext#addCompletionCallback(Runnable, int)
 * @see UnmarshalElement
 */
public class ReferenceConverter implements Converter {

	private static Field referenceElement;

	private Converter nested;

	public ReferenceConverter(XStream xstream) {
		xstream.omitField(Reference.class, "element");

		nested = xstream.getConverterLookup().lookupConverterForType(
				Object.class);
	}

	public boolean canConvert(Class clazz) {
		return Reference.class.isAssignableFrom(clazz);
	}

	/**
	 * @see #marshalElement(Reference, HierarchicalStreamWriter, MarshallingContext)
	 */
	public void marshal(Object value, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		Reference reference = (Reference) value;

		marshalElement(reference, writer, context);

		nested.marshal(reference, writer, context);
	}

	/**
	 * Write the element of a reference as an id attribute.
	 * 
	 * @param reference
	 *            reference to write element for
	 * @param writer
	 *            the writer
	 * @param context
	 *            the context
	 */
	protected void marshalElement(Reference reference,
			HierarchicalStreamWriter writer, MarshallingContext context) {
		writer.addAttribute("id", ElementConverter.Marshal
				.get(context).getId(reference.getElement()));
	}

	/**
	 * @see UnmarshalElement
	 */
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {

		Reference reference = (Reference) nested.unmarshal(reader, context);

		context.addCompletionCallback(new UnmarshalElement(reference, reader
				.getAttribute("id"), context), 0);

		return reference;
	}

	/**
	 * Read the element of a reference.
	 */
	protected static class UnmarshalElement implements Runnable {

		private Reference reference;

		private String id;

		private UnmarshallingContext context;

		private UnmarshalElement(Reference reference, String id,
				UnmarshallingContext context) {
			this.reference = reference;
			this.id = id;
			this.context = context;
		}

		public void run() {
			Element element = ElementConverter.Unmarshal.get(context)
					.getElement(id);

			try {
				if (referenceElement == null) {
					referenceElement = Reference.class
							.getDeclaredField("element");
					referenceElement.setAccessible(true);
				}
				referenceElement.set(reference, element);
			} catch (Exception ex) {
				throw new ConversionException(ex);
			}
		}
	}
}