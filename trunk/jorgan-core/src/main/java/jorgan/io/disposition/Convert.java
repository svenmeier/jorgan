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

public class Convert {

	private Pattern pattern;

	private String xsl;

	public Convert(String pattern, String xsl) {
		this.pattern = Pattern.compile(pattern);
		this.xsl = xsl;
	}

	public String getPattern() {
		return this.pattern.toString();
	}

	public boolean isApplicable(String version) {
		return pattern.matcher(version).matches();
	}

	@Override
	public String toString() {
		return xsl;
	}

	public void exists() {
		if (getSource() == null) {
			throw new IllegalStateException(xsl);
		}
	}

	public InputStream convert(InputStream in) throws IOException {
		TransformerFactory factory = TransformerFactory.newInstance();
		factory.setAttribute("indent-number", new Integer(4));

		Transformer transform;
		try {
			InputStream stream = getSource();

			transform = factory.newTransformer(new StreamSource(stream));

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

	private InputStream getSource() {
		return Convert.class.getResourceAsStream("conversion/" + xsl);
	}
}
