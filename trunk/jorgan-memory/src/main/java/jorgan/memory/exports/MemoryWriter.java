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

	public MemoryWriter(Storage storage, Range range) {
		if (!storage.isLoaded()) {
			throw new IllegalArgumentException("no memory");
		}
		this.storage = storage;
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
		String name = Elements.getDisplayName(reference.getElement());

		writer.write(String.format("       %4s %s\n", format.format(state),
				name));
	}

	private void writeCombination(Writer writer, Combination combination)
			throws IOException {
		writer.write(String.format("      %s\n", Elements
				.getDisplayName(combination)));
	}

	private void writeLevel(Writer writer, int l) throws IOException {
		writer.write(String.format("%s: %s\n", l + 1, storage.getTitle(l)));
	}
}