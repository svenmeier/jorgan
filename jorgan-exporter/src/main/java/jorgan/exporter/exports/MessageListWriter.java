package jorgan.exporter.exports;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import jorgan.disposition.Element;
import jorgan.disposition.Elements;
import jorgan.disposition.Message;
import jorgan.midi.mpl.Tuple;

public class MessageListWriter extends NamingWriter {

	private List<Element> elements;

	public MessageListWriter(List<Element> elements) {
		this.elements = elements;
	}

	public void write(Writer writer) throws IOException {
		for (Element element : elements) {
			for (Message message : element.getMessages()) {
				writer.write(getName(element));
				writer.write("\t");

				writer.write(Elements.getDisplayName(message.getClass()));
				writer.write("\t");

				Tuple tuple = message.getTuple();
				for (int c = 0; c < tuple.getLength(); c++) {
					writer.write(tuple.get(c).toString());
					writer.write("\t");
				}

				writer.write("\n");
			}
		}
	}
}
