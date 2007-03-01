/**
 * 
 */
package jorgan.io.disposition;

import jorgan.disposition.Key;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class KeyConverter implements Converter {

	public boolean canConvert(Class clazz) {
		return clazz.equals(Key.class);
	}

	public void marshal(Object value, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		Key key = (Key) value;

		writer.setValue("" + key.getName());
	}

	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {

		return new Key(reader.getValue());
	}
}