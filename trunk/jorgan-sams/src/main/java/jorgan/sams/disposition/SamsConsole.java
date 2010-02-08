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
package jorgan.sams.disposition;

import jorgan.disposition.Console;

/**
 */
public class SamsConsole extends Console {

	private long duration;

	public void setDuration(long duration) {
		if (this.duration != duration) {
			long oldDuration = this.duration;

			this.duration = duration;

			fireChange(new PropertyChange(oldDuration, this.duration));
		}
	}

	public long getDuration() {
		return duration;
	}

	public Encoding getEncoding() {
		return new NoteOnOffEncoding();
	}
}