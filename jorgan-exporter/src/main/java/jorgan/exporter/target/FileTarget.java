package jorgan.exporter.target;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import jorgan.exporter.gui.Export;

public class FileTarget implements Target {

	private File file;

	public FileTarget(File file) {
		this.file = file;
	}

	public void export(Export aExport) throws IOException {
		OutputStream output = new BufferedOutputStream(new FileOutputStream(
				file));

		try {
			aExport.stream(output);
		} finally {
			output.close();
		}
	}
}
