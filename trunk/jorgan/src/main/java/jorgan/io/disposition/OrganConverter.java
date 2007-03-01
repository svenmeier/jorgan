/**
 * 
 */
package jorgan.io.disposition;

import java.lang.reflect.Field;

import jorgan.App;
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
		writer.addAttribute("version", App.getVersion());
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