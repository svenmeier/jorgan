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
package jorgan.disposition;

import java.util.List;

import jorgan.disposition.Message.OutputMessage;

/**
 * An activateable filter.
 */
public class ActivateableFilter extends Activateable implements Filter {

	public List<Class<? extends Message>> getMessageClasses() {
		List<Class<? extends Message>> messages = super.getMessageClasses();

		messages.add(Intercept.class);
		messages.add(Engaged.class);
		messages.add(Disengaged.class);

		return messages;
	}

	public static class Engaged extends OutputMessage {
	}

	public static class Disengaged extends OutputMessage {
	}
}