package jorgan.memory.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import jorgan.gui.file.DispositionFileFilter;
import jorgan.io.disposition.Conversion;

import com.thoughtworks.xstream.converters.ConversionException;

public class Extract {

	private TransformerFactory factory = TransformerFactory.newInstance();

	public Extract() {
	}

	private InputStream convert(InputStream in) throws ConversionException,
			IOException {

		in = new BufferedInputStream(in);

		String version = Conversion.getVersion(in);

		boolean apply = false;
		for (Conversion conversion : Conversion.list) {
			if (conversion.getPattern().startsWith("3\\.7")) {
				break;
			}

			if (apply || conversion.isApplicable(version)) {
				apply = true;

				in = conversion.convert(in);
			}
		}

		return in;
	}

	public void extract(InputStream in, OutputStream out) throws IOException {

		Transformer transform;
		try {
			transform = factory.newTransformer(new StreamSource(Extract.class
					.getResourceAsStream("extract.xsl")));

			transform.setOutputProperty(OutputKeys.INDENT, "yes");

			transform.transform(new StreamSource(in), new StreamResult(out));
		} catch (TransformerException e) {
			IOException ex = new IOException();
			ex.initCause(e);
			throw ex;
		}
	}

	private void now(File from, File to) throws IOException {
		InputStream in = new FileInputStream(from);
		OutputStream out = new FileOutputStream(to);

		try {
			in = convert(in);

			extract(in, out);
		} finally {
			in.close();
			out.close();
		}
	}

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("supply disposition file name");
			System.exit(1);
			return;
		}

		File in = new File(args[0]);

		String name = args[0];
		if (name.endsWith(DispositionFileFilter.FILE_SUFFIX)) {
			name = name.substring(0, name.length()
					- DispositionFileFilter.FILE_SUFFIX.length());
		}
		File out = new File(name + ".memory");

		try {
			new Extract().now(in, out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
