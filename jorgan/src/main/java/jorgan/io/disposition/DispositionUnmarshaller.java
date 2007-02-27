/**
 * 
 */
package jorgan.io.disposition;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import jorgan.disposition.Element;
import jorgan.disposition.Organ;
import jorgan.disposition.Reference;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.DataHolder;
import com.thoughtworks.xstream.converters.ErrorWriter;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.core.MapBackedDataHolder;
import com.thoughtworks.xstream.core.util.FastStack;
import com.thoughtworks.xstream.core.util.PrioritizedList;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.mapper.Mapper;

public class DispositionUnmarshaller implements UnmarshallingContext {

	protected HierarchicalStreamReader reader;

	private ConverterLookup converterLookup;

	private Mapper mapper;

	private FastStack types = new FastStack(16);

	private DataHolder dataHolder;

	private final PrioritizedList validationList = new PrioritizedList();

	private Map<String, Element> elementIds = new HashMap<String, Element>();
	
	private Organ organ;
	
	public DispositionUnmarshaller(
			HierarchicalStreamReader reader, ConverterLookup converterLookup,
			Mapper mapper) {
		this.reader = reader;
		this.converterLookup = converterLookup;
		this.mapper = mapper;
	}

	public Object convertAnother(Object parent, Class type) {
		Converter converter = converterLookup.lookupConverterForType(type);
		return convert(parent, type, converter);
	}

	public Object convertAnother(Object parent, Class type, Converter converter) {
		return convert(parent, type, converter);
	}

	protected Object convert(Object parent, Class type, Converter converter) {
		try {
            types.push(mapper.defaultImplementationOf(type));
			Object result = converter.unmarshal(reader, this);
            types.popSilently();

            if (result instanceof Element) {
				final Element element = (Element) result;
				final String id = reader.getAttribute("id");
				addCompletionCallback(new Runnable() {
					public void run() {
						handleElement(element, id);
					}
				}, 1);
			}

            if (result instanceof Reference) {
				final Reference reference = (Reference) result;
				final String id = reader.getAttribute("id");
				addCompletionCallback(new Runnable() {
					public void run() {
						handleReference(reference, id);
					}
				}, 0);
			}

			return result;
		} catch (ConversionException conversionException) {
			addInformationTo(conversionException, type);
			throw conversionException;
		} catch (RuntimeException e) {
			ConversionException conversionException = new ConversionException(e);
			addInformationTo(conversionException, type);
			throw conversionException;
		}
	}

	private static Field elementOrgan;

	private static Field referenceElement;

	private void handleElement(Element element, String id) {
		elementIds.put(id, element);
		try {
			if (elementOrgan == null) {
				elementOrgan = Element.class.getDeclaredField("organ");
				elementOrgan.setAccessible(true);
			}
			elementOrgan.set(element, organ);
		} catch (Exception ex) {
			throw new ConversionException(ex);
		}
	}

	private void handleReference(Reference reference, String id) {
		try {
			if (referenceElement == null) {
				referenceElement = Reference.class.getDeclaredField("element");
				referenceElement.setAccessible(true);
			}
		} catch (Exception ex) {
			throw new ConversionException(ex);
		}
			
		Element element = elementIds.get(id);
		if (element == null) {
			throw new ConversionException("reference to unkown element '" + id + "'");
		}

		try {
			referenceElement.set(reference, element);
		} catch (Exception ex) {
			throw new ConversionException(ex);
		}
	}

	private void addInformationTo(ErrorWriter errorWriter, Class type) {
		errorWriter.add("class", type.getName());
		errorWriter.add("required-type", getRequiredType().getName());
		reader.appendErrors(errorWriter);
	}

	public void addCompletionCallback(Runnable work, int priority) {
		validationList.add(work, priority);
	}

	public Object currentObject() {
		return null;
	}

	public Class getRequiredType() {
		return (Class) types.peek();
	}

	public Object get(Object key) {
		lazilyCreateDataHolder();
		return dataHolder.get(key);
	}

	public void put(Object key, Object value) {
		lazilyCreateDataHolder();
		dataHolder.put(key, value);
	}

	public Iterator keys() {
		lazilyCreateDataHolder();
		return dataHolder.keys();
	}

	private void lazilyCreateDataHolder() {
		if (dataHolder == null) {
			dataHolder = new MapBackedDataHolder();
		}
	}

	public Object start(DataHolder dataHolder) {
		this.dataHolder = dataHolder;

		Class type;
		type = mapper.realClass(reader.getNodeName());

		organ = (Organ)convertAnother(null, type);

		Iterator validations = validationList.iterator();
		while (validations.hasNext()) {
			Runnable runnable = (Runnable) validations.next();
			runnable.run();
		}

		return organ;
	}
}