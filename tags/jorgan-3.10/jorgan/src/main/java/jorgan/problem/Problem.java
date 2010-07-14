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
package jorgan.problem;

import jorgan.disposition.Element;
import jorgan.util.Null;

/**
 * Problem of a player.
 */
public class Problem {

	private Severity severity;
	
	private Element element;

	private Object location;

	private String message;

	public Problem(Severity severity, Element element, Object location, String message) {
		this.severity = severity;
		this.element = element;
		this.location = location;
		this.message = message;
	}

	public Severity getSeverity() {
		return severity;
	}
	
	public Object getLocation() {
		return location;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public boolean equals(Object object) {
		if (object == null || !(object.getClass() == this.getClass())) {
			return false;
		}

		Problem problem = (Problem) object;

		if (!(this.severity == problem.severity)) {
			return false;
		}

		if (!(this.element == problem.element)) {
			return false;
		}
		
		if (!Null.safeEquals(this.location, problem.location)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return element.hashCode();
	}

	public Element getElement() {
		return element;
	}
}