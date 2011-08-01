/**
 * Bias - POJO Configuration.
 * Copyright (C) 2007 Sven Meier
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package bias.util;

/**
 * Utility for concatenation of strings.
 */
public class Tokenizer {

	// separator starts with first printable ASCII character ' ' (0x32)
	private static char SEPARATOR = ' ';

	private String[] tokens;

	public Tokenizer(String string) {
		if (string.length() == 0) {
			tokens = new String[0];
		} else {
			tokens = split(string);
		}
	}

	public Tokenizer(String[] tokens) {
		this.tokens = tokens;
	}

	public String[] getTokens() {
		return tokens;
	}

	private String[] split(String string) {
		char separator = string.charAt(0);

		int count = 0;
		int index = 0;
		while (index != -1) {
			count++;
			index = string.indexOf(separator, index + 1);
		}

		String[] tokens = new String[count];
		int from = 0;
		for (int t = 0; t < tokens.length; t++) {
			int to = string.indexOf(separator, from + 1);
			if (to == -1) {
				to = string.length();
			}
			tokens[t] = string.substring(from + 1, to);
			from = to;
		}
		return tokens;
	}
	
	@Override
	public String toString() {
		char separator = SEPARATOR;

		for (int t = 0; t < tokens.length; t++) {
			while (tokens[t].indexOf(separator) != -1) {
				separator++;
			}
		}

		StringBuffer buffer = new StringBuffer();
		for (int t = 0; t < tokens.length; t++) {
			buffer.append(separator);
			buffer.append(tokens[t]);
		}

		return buffer.toString();
	}
}
