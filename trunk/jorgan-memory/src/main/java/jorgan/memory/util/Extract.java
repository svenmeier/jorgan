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
import jorgan.io.DispositionStream;
import jorgan.io.disposition.Conversion;

import com.thoughtworks.xstream.converters.ConversionException;

public class Extract {

	private TransformerFactory factory = TransformerFactory.newInstance();

	public Extract() {
	}

	private InputStream convert(InputStream in) throws ConversionException,
			IOException {

		in = new BufferedInputStream(in);

		String version = DispositionStream.getVersion(in);

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
		if (args.length < 1 || args.length > 2) {
			System.out
					.println("Supply source disposition and target memory file");
			System.exit(1);
			return;
		}

		File in = new File(args[0]);
		File out;
		if (args.length == 1) {
			String name;
			if (args[0].endsWith(DispositionFileFilter.FILE_SUFFIX)) {
				name = args[0].substring(0, args[0].length()
						- DispositionFileFilter.FILE_SUFFIX.length());
			} else {
				name = args[0];
			}
			out = new File(name + ".memory");
		} else {
			out = new File(args[1]);
		}

		try {
			new Extract().now(in, out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
