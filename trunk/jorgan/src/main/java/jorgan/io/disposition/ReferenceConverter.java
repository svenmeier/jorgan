/**
 * 
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
 * @see #writeElement(Reference, HierarchicalStreamWriter, MarshallingContext)
 * @see UnmarshallingContext#addCompletionCallback(Runnable, int)
 * @see ElementLinker
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

	public void marshal(Object value, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		Reference reference = (Reference) value;

		writeElement(reference, writer, context);

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
	protected void writeElement(Reference reference,
			HierarchicalStreamWriter writer, MarshallingContext context) {
		writer.addAttribute("id", ElementConverter.Marshal
				.get(context).getId(reference.getElement()));
	}

	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {

		Reference reference = (Reference) nested.unmarshal(reader, context);

		context.addCompletionCallback(new ElementLinker(reference, reader
				.getAttribute("id"), context), 0);

		return reference;
	}

	/**
	 * Linker of a reference to its element.
	 */
	protected static class ElementLinker implements Runnable {

		private Reference reference;

		private String id;

		private UnmarshallingContext context;

		private ElementLinker(Reference reference, String id,
				UnmarshallingContext context) {
			this.reference = reference;
			this.id = id;
			this.context = context;
		}

		public void run() {
			Element element = ElementConverter.Unmarshal.get(context)
					.getElement(id);
			if (element == null) {
				throw new ConversionException("unkown element '" + id + "'");
			}

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