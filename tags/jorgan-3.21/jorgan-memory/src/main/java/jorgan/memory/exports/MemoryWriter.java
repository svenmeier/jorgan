package jorgan.memory.exports;

import java.io.IOException;
import java.io.Writer;
import java.text.Format;

import jorgan.disposition.Combination;
import jorgan.disposition.Element;
import jorgan.disposition.Elements;
import jorgan.disposition.Reference;
import jorgan.memory.Storage;

public class MemoryWriter {

	private Format format = new StateFormat();

	private Storage storage;

	private Range range = new Range(0, 0);

	private boolean useDescriptionName;

	private boolean activeSwitchesOnly;

	public MemoryWriter(Storage storage) {
		if (!storage.isLoaded()) {
			throw new IllegalArgumentException("no memory");
		}
		this.storage = storage;
	}

	public void setRange(Range range) {
		this.range = range;
	}

	public void setUseDescriptionName(boolean useDescriptionName) {
		this.useDescriptionName = useDescriptionName;
	}

	public void setActiveSwitchesOnly(boolean activeSwitchesOnly) {
		this.activeSwitchesOnly = activeSwitchesOnly;
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

		if (Boolean.FALSE.equals(state) && activeSwitchesOnly) {
			return;
		}

		writer.write(String.format("\t\t%s\t%s\n", format.format(state), name));
	}

	private void writeCombination(Writer writer, Combination combination)
			throws IOException {
		writer.write(String.format("\t%s\n", getName(combination)));
	}

	private void writeLevel(Writer writer, int l) throws IOException {
		writer.write(String.format("%s: %s\n", l + 1, storage.getTitle(l)));
	}

	private String getName(Element element) {
		String name;

		if (useDescriptionName) {
			name = Elements.getDescriptionName(element);
		} else {
			name = Elements.getDisplayName(element);
		}

		return name;
	}
}