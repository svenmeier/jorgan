package jorgan.memory.util;

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

public class Extract {

	private TransformerFactory factory = TransformerFactory.newInstance();

	public Extract() {
	}

	public void convert(InputStream in, OutputStream out) throws IOException {

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
			convert(in, out);
		} finally {
			in.close();
			out.close();
		}
	}

	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Supply source disposition and target memory file");
			System.exit(1);
			return;
		}
		
		try {
			new Extract().now(new File(args[0]), new File(args[1]));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
