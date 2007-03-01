/**
 * 
 */
package jorgan.io.disposition;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class BooleanArrayConverter implements Converter {

	private boolean[] tmp = new boolean[0];

	public boolean canConvert(Class clazz) {
		return tmp.getClass() == clazz;
	}

	public void marshal(Object value, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		boolean[] bs = (boolean[]) value;

		StringBuffer buffer = new StringBuffer(bs.length);
		for (boolean b : bs) {
			buffer.append(b ? "1" : "0");
		}

		writer.setValue(buffer.toString());
	}

	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {

		String value = reader.getValue();

		boolean[] bs = new boolean[value.length()];
		for (int b = 0; b < bs.length; b++) {
			bs[b] = value.charAt(b) == '1' ? true : false;
		}
		return bs;
	}
}