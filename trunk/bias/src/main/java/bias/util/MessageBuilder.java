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

import java.text.MessageFormat;

/**
 * A builder of messages.
 */
public class MessageBuilder {

	private static final String NONE = "???";
	
	private String pattern = NONE;

	public MessageBuilder() {
	}

	public MessageBuilder(String pattern) {
		this.pattern = pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public boolean hasPattern() {
		return pattern != NONE;
	}
	
	public String getPattern() {
		return pattern;
	}

	public String build(Object... arguments) {
		return MessageFormat.format(pattern, arguments);
	}
}