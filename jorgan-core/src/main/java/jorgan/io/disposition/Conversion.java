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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParserException;

import com.thoughtworks.xstream.converters.ConversionException;

public class Conversion {

	private static final Logger logger = Logger.getLogger(Conversion.class
			.getName());

	private static Convert[] list = new Convert[] {
			new Convert("2\\.0-.*", "convert-2.0.xsl"),
			new Convert("2\\.0", "convert-2.1-beta.xsl"),
			new Convert("2\\.1-beta.*", "convert-2.1.xsl"),
			new Convert("2\\.1.*", "convert-2.2-beta.xsl"),
			new Convert("2\\.2-beta.*", "convert-2.2.xsl"),
			new Convert("2\\.2.*", "convert-2.3-beta.xsl"),
			new Convert("2\\.3-beta.*", "convert-2.3.xsl"),
			new Convert("2\\.3.*", "convert-2.4-beta.xsl"),
			new Convert("2\\.4-beta.*", "convert-2.4.xsl"),
			new Convert("2\\.4.*", "convert-3.0-beta.xsl"),
			new Convert("3\\.0-beta.*", "convert-3.0.xsl"),
			new Convert("3\\.0", "convert-3.1.xsl"),
			new Convert("3\\.(1|2)(\\..*)?", "convert-3.3.xsl"),
			new Convert("3\\.(3|4)(\\..*)?", "convert-3.5-beta.xsl"),
			new Convert("3\\.5-beta.*", "convert-3.5.xsl"),
			new Convert("3\\.5", "convert-3.5.1.xsl"),
			new Convert("3\\.(5|6)(\\..*)?", "convert-3.7.xsl"),
			new Convert("3\\.7", "convert-3.8.xsl"),
			new Convert("3\\.8.*", "convert-3.9-beta.xsl"),
			new Convert("3\\.9-beta.*", "convert-3.9.xsl"),
			new Convert("3\\.(9|10|11-beta)(\\..*)?", "convert-3.11-beta.xsl"),
			new Convert("3\\.11-beta.*", "convert-3.11.xsl"),
			new Convert("3\\.(11|12)(\\..*)?", "convert-3.13-beta.xsl"),
			new Convert("3\\.13-beta(1|2|3).*", "convert-3.13.xsl"),
			new Convert("3\\.(14|15-beta).*", "convert-3.15.xsl"),
			new Convert("3\\.(15|16|17|18|19|20-beta1).*", "convert-3.20.xsl") };

	public BufferedInputStream convert(InputStream in)
			throws ConversionException, IOException {

		BufferedInputStream buffered = new BufferedInputStream(in);

		String version = getVersion(buffered);

		boolean apply = false;
		for (Convert convert : list) {
			if (apply || convert.isApplicable(version)) {
				apply = true;

				logger.log(Level.INFO, "applying '" + convert + "'");

				buffered = new BufferedInputStream(convert.convert(buffered));
			}
		}

		return buffered;
	}

	private String getVersion(InputStream in) throws IOException {

		// make sure the parse doesn't step over the mark limit
		byte[] header = new byte[2048];
		in.mark(header.length);
		in.read(header, 0, header.length);
		in.reset();

		String version;
		try {
			KXmlParser parser = new KXmlParser();

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
