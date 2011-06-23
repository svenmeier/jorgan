package jorgan.memory.gui.exports;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

import javax.swing.JComponent;

import jorgan.exporter.gui.Export;
import jorgan.memory.Storage;
import jorgan.memory.exports.MemoryWriter;
import jorgan.session.OrganSession;
import jorgan.swing.wizard.AbstractPage;
import jorgan.swing.wizard.Page;
import bias.Configuration;

public class MemoryExport implements Export {

	private static Configuration config = Configuration.getRoot().get(
			MemoryExport.class);

	private String name;

	private String description;

	private OptionsPanel panel;

	private Storage storage;

	public MemoryExport(OrganSession session) {
		config.read(this);

		storage = session.lookup(Storage.class);

		if (!storage.isLoaded()) {
			throw new IllegalArgumentException("no memory");
		}
	}

	@Override
	public List<Page> getPages() {
		return Collections.<Page> singletonList(new OptionsPage());
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

		new MemoryWriter(storage, panel.getUseDescriptionName(), panel
				.getRange()).write(writer);

		writer.flush();
	}

	private class OptionsPage extends AbstractPage {

		@Override
		public String getDescription() {
			return MemoryExport.this.getDescription();
		}

		@Override
		protected JComponent getComponentImpl() {
			if (panel == null) {
				panel = new OptionsPanel(storage.getSize());
			}
			return panel;
		}
	}
}