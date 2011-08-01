package jorgan.exporter.target;

import java.io.IOException;

import jorgan.exporter.gui.Export;

public interface Target {

	public void export(Export aExport) throws IOException;
}
