package jorgan.memory.gui.exports;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

import jorgan.disposition.Combination;
import jorgan.disposition.Continuous;
import jorgan.disposition.Elements;
import jorgan.disposition.Switch;
import jorgan.exporter.gui.Export;
import jorgan.memory.Storage;
import jorgan.memory.disposition.Memory;
import jorgan.session.OrganSession;
import jorgan.swing.wizard.Page;
import bias.Configuration;

public class MemoryExport implements Export {

	private static Configuration config = Configuration.getRoot().get(
			MemoryExport.class);

	private String name;

	private String description;

	private OrganSession session;

	public MemoryExport(OrganSession session) {
		config.read(this);

		this.session = session;
	}

	@Override
	public List<Page> getPages() {
		return Collections.<Page> emptyList();
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getMimeType() {
		return "text";
	}

	@Override
	public void stream(OutputStream output) throws IOException {
		Writer writer = new OutputStreamWriter(output, Charset.forName("UTF-8"));

		write(writer);

		writer.flush();
	}

	private void write(Writer writer) throws IOException {
		Memory memory = session.getOrgan().getElement(Memory.class);
		if (memory == null) {
			throw new IOException("no memory");
		}

		Storage storage;
		try {
			storage = session.lookup(Storage.class);
		} catch (IllegalArgumentException ex) {
			throw new IOException(ex);
		}

		for (int l = 0; l < memory.getSize(); l++) {
			writer.write(String.format("%s : %s\n", l, storage.getTitle(l)));

			for (Combination combination : memory
					.getReferenced(Combination.class)) {

				writer.write(String.format("  %s\n", Elements
						.getDisplayName(combination)));

				for (Switch aSwitch : combination.getReferenced(Switch.class)) {

					writer.write(String.format("    %s\n", Elements
							.getDisplayName(aSwitch)));
				}

				for (Continuous continuous : combination
						.getReferenced(Continuous.class)) {

					writer.write(String.format("    %s\n", Elements
							.getDisplayName(continuous)));
				}
			}
		}
	}
}