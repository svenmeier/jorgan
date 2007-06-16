package jorgan.io.disposition;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class Conversion {

	private static final Logger logger = Logger.getLogger(Conversion.class
			.getName());

	private static final boolean DEBUG = false;

	private static Conversion[] conversions = new Conversion[] {
			new Conversion("<organ>", "convert1.0To2.0-beta.xsl"),
			new Conversion("<organ *version=\"(2\\.0-beta.*|2\\.0-RC1)\" *>",
					"convert2.0-betaTo2.0.xsl"),
			new Conversion("<organ *version=\"2\\.0\" *>",
					"convert2.0To2.1-beta.xsl"),
			new Conversion("<organ *version=\"2\\.1-beta.*\" *>",
					"convert2.1-betaTo2.1.xsl"),
			new Conversion("<organ *version=\"2\\.1.*\" *>",
					"convert2.1To2.2-beta.xsl"),
			new Conversion("<organ *version=\"2\\.2-beta.*\" *>",
					"convert2.2-betaTo2.2.xsl"),
			new Conversion("<organ *version=\"2\\.2.*\" *>",
					"convert2.2To2.3-beta.xsl"),
			new Conversion("<organ *version=\"2\\.3-beta.*\" *>",
					"convert2.3-betaTo2.3.xsl"),
			new Conversion("<organ *version=\"2\\.3.*\" *>",
					"convert2.3To2.4-beta.xsl"),
			new Conversion("<organ *version=\"2\\.4-beta.*\" *>",
					"convert2.4-betaTo2.4.xsl") };

	private String pattern;

	private String xsl;

	public Conversion(String pattern, String xsl) {
		this.pattern = pattern;
		this.xsl = xsl;
	}

	public boolean isApplicable(String header) {
		Pattern pattern = Pattern.compile(this.pattern);
		Matcher matcher = pattern.matcher(header);

		return matcher.find();
	}

	public InputStream convert(InputStream in) throws TransformerException {
		Transformer transform = TransformerFactory.newInstance()
				.newTransformer(
						new StreamSource(Conversion.class
								.getResourceAsStream(xsl)));

		ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();

		transform.transform(new StreamSource(in),
				new StreamResult(byteArrayOut));

		if (DEBUG) {
			System.out.println(new String(byteArrayOut.toByteArray()));
			System.out.flush();
		}

		in = new ByteArrayInputStream(byteArrayOut.toByteArray());

		return in;
	}

	public static InputStream convertAll(InputStream in)
			throws TransformerException, IOException {

		in = new BufferedInputStream(in);

		String header = getHeader(in);

		int index = 0;
		while (index < conversions.length) {
			if (conversions[index].isApplicable(header)) {
				break;
			}
			index++;
		}

		while (index < conversions.length) {
			logger.log(Level.INFO, "converting '" + conversions[index].pattern
					+ "'");

			in = conversions[index].convert(in);
			index++;
		}

		return in;
	}

	private static String getHeader(InputStream in) throws IOException {
		in.mark(2048);

		byte[] bytes = new byte[1024];
		int offset = 0;
		while (offset != -1 && offset < bytes.length) {
			offset = in.read(bytes, offset, bytes.length - offset);
		}

		in.reset();

		String header = new String(bytes, "UTF-8");

		return header;
	}
}
