package jorgan.memory.exports;

import java.io.IOException;
import java.io.Writer;
import java.text.Format;

import jorgan.disposition.Combination;
import jorgan.disposition.Element;
import jorgan.disposition.Elements;
import jorgan.disposition.Reference;
import jorgan.memory.Storage;
import jorgan.memory.gui.exports.StateFormat;

public class MemoryWriter {

	private Format format = new StateFormat();

	private Storage storage;

	private Range range;

	private boolean useDescriptionName;

	public MemoryWriter(Storage storage, boolean useDescriptionName, Range range) {
		if (!storage.isLoaded()) {
			throw new IllegalArgumentException("no memory");
		}
		this.storage = storage;
		this.useDescriptionName = useDescriptionName;
		this.range = range;
	}

	public void write(Writer writer) throws IOException {

		for (int level : range) {
			writeLevel(writer, level);

			for (Combination combination : storage.getMemory().getReferenced(
					Combination.class)) {

				writeCombination(writer, combination);

				for (Reference<? extends Element> reference : combination
						.getReferences()) {
					writeReference(writer, level, combination, reference);
				}
			}
		}
	}

	private void writeReference(Writer writer, int level,
			Combination combination, Reference<? extends Element> reference)
			throws IOException {

		Object state = storage.getState().get(combination, reference, level);
		String name = getName(reference.getElement());

		writer.write(String.format("       %4s %s\n", format.format(state),
				name));
	}

	private void writeCombination(Writer writer, Combination combination)
			throws IOException {
		writer.write(String.format("      %s\n", getName(combination)));
	}

	private void writeLevel(Writer writer, int l) throws IOException {
		writer.write(String.format("%s: %s\n", l + 1, storage.getTitle(l)));
	}

	private String getName(Element element) {
		String name = null;

		if (useDescriptionName) {
			name = element.getTexts().get("name");
		}

		if (name == null) {
			name = Elements.getDisplayName(element);
		}

		return name;
	}
}