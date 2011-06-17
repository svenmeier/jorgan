package jorgan.memory.gui;

import java.util.ArrayList;
import java.util.List;

import jorgan.exporter.gui.Export;
import jorgan.exporter.gui.spi.ExportProvider;
import jorgan.memory.disposition.Memory;
import jorgan.memory.gui.exports.MemoryExport;
import jorgan.session.OrganSession;

public class MemoryExportProvider implements ExportProvider {
	@Override
	public List<Export> getExports(OrganSession session) {
		List<Export> exports = new ArrayList<Export>();

		Memory memory = session.getOrgan().getElement(Memory.class);
		if (memory != null) {
			exports.add(new MemoryExport(session));
		}

		return exports;
	}
}
