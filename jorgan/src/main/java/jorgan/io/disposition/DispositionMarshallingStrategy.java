/**
 * 
 */
package jorgan.io.disposition;

import com.thoughtworks.xstream.MarshallingStrategy;
import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.DataHolder;
import com.thoughtworks.xstream.core.DefaultConverterLookup;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

@SuppressWarnings("deprecation")
public class DispositionMarshallingStrategy implements MarshallingStrategy {

	public void marshal(HierarchicalStreamWriter writer, Object obj,
			ConverterLookup converterLookup, Mapper mapper,
			DataHolder dataHolder) {
		new DispositionMarshaller(writer, converterLookup, mapper).start(obj,
				dataHolder);
	}

	public Object unmarshal(Object root, HierarchicalStreamReader reader,
			DataHolder dataHolder, ConverterLookup converterLookup,
			Mapper mapper) {
		return new DispositionUnmarshaller(reader, converterLookup, mapper)
				.start(dataHolder);
	}

	public void marshal(HierarchicalStreamWriter writer, Object obj,
			DefaultConverterLookup converterLookup, ClassMapper classMapper,
			DataHolder dataHolder) {
		marshal(writer, obj, converterLookup, (Mapper) classMapper, dataHolder);
	}

	public Object unmarshal(Object root, HierarchicalStreamReader reader,
			DataHolder dataHolder, DefaultConverterLookup converterLookup,
			ClassMapper classMapper) {
		return unmarshal(root, reader, dataHolder, converterLookup,
				(Mapper) classMapper);
	}
}