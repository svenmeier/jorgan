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

import org.xmlpull.mxp1.MXParser;
import org.xmlpull.v1.XmlPullParserException;

import com.thoughtworks.xstream.converters.ConversionException;

public class Conversion {

	private static final Logger logger = Logger.getLogger(Conversion.class
			.getName());

	private static Convert[] list = new Convert[] {
			new Convert("2\\.0-.*", "convert2.0-betaTo2.0.xsl"),
			new Convert("2\\.0", "convert2.0To2.1-beta.xsl"),
			new Convert("2\\.1-beta.*", "convert2.1-betaTo2.1.xsl"),
			new Convert("2\\.1.*", "convert2.1To2.2-beta.xsl"),
			new Convert("2\\.2-beta.*", "convert2.2-betaTo2.2.xsl"),
			new Convert("2\\.2.*", "convert2.2To2.3-beta.xsl"),
			new Convert("2\\.3-beta.*", "convert2.3-betaTo2.3.xsl"),
			new Convert("2\\.3.*", "convert2.3To2.4-beta.xsl"),
			new Convert("2\\.4-beta.*", "convert2.4-betaTo2.4.xsl"),
			new Convert("2\\.4.*", "convert2.4To3.0-beta.xsl"),
			new Convert("3\\.0-beta.*", "convert3.0-betaTo3.0.xsl"),
			new Convert("3\\.0", "convert3.0To3.1.xsl"),
			new Convert("3\\.(1|2.*)", "convert3.1To3.3.xsl"),
			new Convert("3\\.(3|4).*", "convert3.3To3.5-beta.xsl"),
			new Convert("3\\.5-beta.*", "convert3.5-betaTo3.5.xsl"),
			new Convert("3\\.5", "convert3.5To3.5.1.xsl"),
			new Convert("3\\.(5|6).*", "convert3.6To3.7.xsl"),
			new Convert("3\\.7", "convert3.7To3.8.xsl"),
			new Convert("3\\.8.*", "convert3.8To3.9-beta.xsl"),
			new Convert("3\\.9-beta.*", "convert3.9-betaTo3.9.xsl"),
			new Convert("3\\.(9|10).*", "convert3.10To3.11-beta.xsl"),
			new Convert("3\\.11-beta.*", "convert3.11-betaTo3.11.xsl") };

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
