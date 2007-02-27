/**
 * 
 */
package jorgan.io.disposition;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import jorgan.App;
import jorgan.disposition.Element;
import jorgan.disposition.Reference;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.DataHolder;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.core.MapBackedDataHolder;
import com.thoughtworks.xstream.core.SequenceGenerator;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriterHelper;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

public class DispositionMarshaller implements MarshallingContext {

	private SequenceGenerator idGenerator = new SequenceGenerator(1);

	private Map<Element, String> elementIds = new HashMap<Element, String>();

	protected HierarchicalStreamWriter writer;

	protected ConverterLookup converterLookup;

	Mapper mapper;

	private DataHolder dataHolder;

	public DispositionMarshaller(HierarchicalStreamWriter writer,
			ConverterLookup converterLookup, Mapper mapper) {
		this.writer = writer;
		this.converterLookup = converterLookup;
		this.mapper = mapper;
	}

	public void convertAnother(Object item) {
		if (item instanceof Element) {
			handleElement((Element) item);
		}
		if (item instanceof Reference) {
			handleReference((Reference) item);
		}

		Converter converter = converterLookup.lookupConverterForType(item
				.getClass());
		convert(item, converter);
	}

	private String getId(Element element) {
		String id = elementIds.get(element);
		if (id == null) {
			id = idGenerator.next();
			elementIds.put(element, id);
		}
		return id;
	}
	
	private void handleReference(Reference reference) {
		writer.addAttribute("id", getId(reference.getElement()));
	}

	private void handleElement(Element element) {
		writer.addAttribute("id", getId(element));
	}

	public void convertAnother(Object item, Converter converter) {
		convert(item, converter);
	}

	protected void convert(Object item, Converter converter) {
		converter.marshal(item, writer, this);
	}

	public void start(Object item, DataHolder dataHolder) {
		this.dataHolder = dataHolder;

		ExtendedHierarchicalStreamWriterHelper.startNode(writer, mapper
				.serializedClass(item.getClass()), item.getClass());
		writer.addAttribute("version", App.getVersion());
		convertAnother(item);
		writer.endNode();
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

	protected Mapper getMapper() {
		return this.mapper;
	}
}