package jorgan.exporter.target;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import jorgan.exporter.gui.Export;

public class ClipboardTarget implements Target {

	public ClipboardTarget() {
	}

	public void export(Export aExport) throws IOException {
		if (!"text".equals(aExport.getMimeType())) {
			throw new IOException("unsupported mime type '"
					+ aExport.getMimeType() + "'");
		}

		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

		clipboard.setContents(new StringSelection(write(aExport)), null);
	}

	private String write(Export aExport) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		aExport.stream(output);

		return new String(output.toByteArray());
	}
}
