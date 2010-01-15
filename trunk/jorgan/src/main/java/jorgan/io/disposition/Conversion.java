/*
 * jOrgan - Java Virtual Organ
 * Copyright (C) 2003 Sven Meier
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package jorgan.io.disposition;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.xmlpull.mxp1.MXParser;
import org.xmlpull.v1.XmlPullParserException;

public class Conversion {

	public static Conversion[] list = new Conversion[] {
			new Conversion("2\\.0-.*", "convert2.0-betaTo2.0.xsl"),
			new Conversion("2\\.0", "convert2.0To2.1-beta.xsl"),
			new Conversion("2\\.1-beta.*", "convert2.1-betaTo2.1.xsl"),
			new Conversion("2\\.1.*", "convert2.1To2.2-beta.xsl"),
			new Conversion("2\\.2-beta.*", "convert2.2-betaTo2.2.xsl"),
			new Conversion("2\\.2.*", "convert2.2To2.3-beta.xsl"),
			new Conversion("2\\.3-beta.*", "convert2.3-betaTo2.3.xsl"),
			new Conversion("2\\.3.*", "convert2.3To2.4-beta.xsl"),
			new Conversion("2\\.4-beta.*", "convert2.4-betaTo2.4.xsl"),
			new Conversion("2\\.4.*", "convert2.4To3.0-beta.xsl"),
			new Conversion("3\\.0-beta.*", "convert3.0-betaTo3.0.xsl"),
			new Conversion("3\\.0", "convert3.0To3.1.xsl"),
			new Conversion("3\\.[1|2].*", "convert3.1To3.3.xsl"),
			new Conversion("3\\.[3|4].*", "convert3.3To3.5-beta.xsl"),
			new Conversion("3\\.5-beta.*", "convert3.5-betaTo3.5.xsl"),
			new Conversion("3\\.5", "convert3.5To3.5.1.xsl"),
			new Conversion("3\\.[5|6].*", "convert3.6To3.7.xsl"),
			new Conversion("3\\.7", "convert3.7To3.8.xsl"),
			new Conversion("3\\.8", "convert3.8To3.9.xsl") };

	private Pattern pattern;

	private String xsl;

	public Conversion(String pattern, String xsl) {
		this.pattern = Pattern.compile(pattern);
		this.xsl = xsl;
	}

	public String getPattern() {
		return this.pattern.toString();
	}

	public boolean isApplicable(String header) {
		return pattern.matcher(header).matches();
	}

	@Override
	public String toString() {
		return xsl;
	}

	public InputStream convert(InputStream in) throws IOException {
		TransformerFactory factory = TransformerFactory.newInstance();
		factory.setAttribute("indent-number", new Integer(4));

		Transformer transform;
		try {
			transform = factory.newTransformer(new StreamSource(
					Conversion.class.getResourceAsStream("conversion/" + xsl)));

			transform.setOutputProperty(OutputKeys.INDENT, "yes");

			File temp = File.createTempFile(xsl + ".", ".xml");

			transform.transform(new StreamSource(in), new StreamResult(temp));

			in.close();

			return new FileInputStream(temp);
		} catch (TransformerException e) {
			IOException ex = new IOException();
			ex.initCause(e);
			throw ex;
		}
	}

	public static String getVersion(InputStream in) throws IOException {

		// make sure the parse doesn't step over the mark limit
		byte[] header = new byte[2048];
		in.mark(header.length);
		in.read(header, 0, header.length);
		in.reset();

		String version;
		try {
			MXParser parser = new MXParser();

			parser.setInput(new ByteArrayInputStream(header), "ASCII");

			parser.nextTag();

			version = parser.getAttributeValue(null, "version");
		} catch (XmlPullParserException e) {
			IOException ex = new IOException();
			ex.initCause(e);
			throw ex;
		}

		return version;
	}
}
