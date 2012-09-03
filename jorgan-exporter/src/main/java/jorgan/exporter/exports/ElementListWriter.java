package jorgan.exporter.exports;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import jorgan.disposition.Element;

public class ElementListWriter extends NamingWriter {

	private List<Element> elements;

	public ElementListWriter(List<Element> elements) {
		this.elements = elements;
	}

	public void write(Writer writer) throws IOException {
		for (Element element : elements) {
			writer.write(getName(element));
			writer.write("\n");
		}
	}
}
