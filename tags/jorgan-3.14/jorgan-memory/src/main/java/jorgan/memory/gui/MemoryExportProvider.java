package jorgan.memory.gui;

import java.util.ArrayList;
import java.util.List;

import jorgan.exporter.gui.Export;
import jorgan.exporter.gui.spi.ExportProvider;
import jorgan.memory.gui.exports.MemoryExport;
import jorgan.session.OrganSession;

public class MemoryExportProvider implements ExportProvider {
	@Override
	public List<Export> getExports(OrganSession session) {
		List<Export> exports = new ArrayList<Export>();

		try {
			exports.add(new MemoryExport(session));
		} catch (IllegalArgumentException noLoadedMemory) {
		}

		return exports;
	}
}
