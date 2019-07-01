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
package jorgan.recorder;

import jorgan.disposition.Console;
import jorgan.disposition.Element;
import jorgan.disposition.Keyboard;
import jorgan.recorder.spi.TrackerProvider;
import jorgan.recorder.tracker.ConsoleTracker;
import jorgan.recorder.tracker.KeyboardTracker;

public class DefaultTrackerProvider implements TrackerProvider {

	public Tracker createTracker(int track, Element element) {
		if (element instanceof Keyboard) {
			return new KeyboardTracker(track, (Keyboard) element);
		} else if (element instanceof Console) {
			return new ConsoleTracker(track, (Console) element);
		} else {
			return null;
		}

	}
}
