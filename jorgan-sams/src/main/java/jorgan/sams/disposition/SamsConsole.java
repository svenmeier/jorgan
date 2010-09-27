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

import java.util.List;

import jorgan.disposition.Console;
import jorgan.disposition.Message;
import jorgan.midi.mpl.Equal;
import jorgan.midi.mpl.Get;
import jorgan.midi.mpl.Set;

/**
 */
public class SamsConsole extends Console {

	private long duration;

	public SamsConsole() {
		addMessage(new OnMagnetOn().change(new Set(144),
				new Set(OnMagnetOn.TAB), new Set(127)));
		addMessage(new OnMagnetOff().change(new Set(144), new Set(
				OnMagnetOff.TAB), new Set(0)));
		addMessage(new OffMagnetOn().change(new Set(128), new Set(
				OffMagnetOn.TAB), new Set(127)));
		addMessage(new OffMagnetOff().change(new Set(128), new Set(
				OffMagnetOff.TAB), new Set(0)));

		addMessage(new TabOn().change(new Equal(144), new Get(TabOn.TAB),
				new Equal(127)));
		addMessage(new TabOff().change(new Equal(128), new Get(TabOff.TAB),
				new Equal(127)));
	}

	public void setDuration(long duration) {
		if (duration < 0) {
			duration = 0;
		}

		if (this.duration != duration) {
			long oldDuration = this.duration;

			this.duration = duration;

			fireChange(new PropertyChange(oldDuration, this.duration));
		}
	}

	public long getDuration() {
		return duration;
	}

	public List<Class<? extends Message>> getMessageClasses() {
		List<Class<? extends Message>> classes = super.getMessageClasses();

		classes.add(OnMagnetOn.class);
		classes.add(OnMagnetOff.class);
		classes.add(OffMagnetOn.class);
		classes.add(OffMagnetOff.class);
		classes.add(TabOn.class);
		classes.add(TabOff.class);

		return classes;
	}

	public static class MagnetMessage extends OutputMessage {
		public static final String TAB = "tab";
	}

	public static class OnMagnetOn extends MagnetMessage {
	}

	public static class OnMagnetOff extends MagnetMessage {
	}

	public static class OffMagnetOn extends MagnetMessage {
	}

	public static class OffMagnetOff extends MagnetMessage {
	}

	public static class TabMessage extends InputMessage {
		public static final String TAB = "tab";
	}

	public static class TabOn extends TabMessage {
	}

	public static class TabOff extends TabMessage {
	}
}