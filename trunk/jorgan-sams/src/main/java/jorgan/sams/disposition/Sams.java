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

import jorgan.disposition.Connector;
import jorgan.disposition.InterceptMessage;
import jorgan.disposition.Message;
import jorgan.midi.mpl.Equal;
import jorgan.midi.mpl.Get;
import jorgan.midi.mpl.Set;

/**
 * Stop action magnets.
 */
public class Sams extends Connector {

	private int maximum = 64;

	private long duration = 250;

	public Sams() {
		addMessage(new TabTurningOn().change(new Equal(144), new Get(
				TabTurningOn.TAB), new Equal(127)));
		addMessage(new CancelTabOn().change(new Set(144), new Set(
				CancelTabOn.TAB), new Set(0)));
		addMessage(new TabTurningOff().change(new Equal(128), new Get(
				TabTurningOff.TAB), new Equal(127)));
		addMessage(new CancelTabOff().change(new Set(128), new Set(
				CancelTabOff.TAB), new Set(0)));

		addMessage(new TabTurnedOn().change(new Equal(144), new Get(
				TabTurnedOn.TAB), new Equal(127)));
		addMessage(new TabTurnedOff().change(new Equal(128), new Get(
				TabTurnedOff.TAB), new Equal(127)));
	}

	public void setMaximum(int maximum) {
		this.maximum = maximum;
	}

	public int getMaximum() {
		return maximum;
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

		classes.add(TabTurningOn.class);
		classes.add(CancelTabOn.class);
		classes.add(TabTurningOff.class);
		classes.add(CancelTabOff.class);
		classes.add(TabTurnedOn.class);
		classes.add(TabTurnedOff.class);

		return classes;
	}

	public static interface TabMessage {
		public static final String TAB = "tab";
	}

	public static class TabTurningOn extends OutputMessage implements
			TabMessage, InterceptMessage {
	}

	public static class CancelTabOn extends OutputMessage implements TabMessage {
	}

	public static class TabTurningOff extends OutputMessage implements
			TabMessage, InterceptMessage {
	}

	public static class CancelTabOff extends OutputMessage implements
			TabMessage {
	}

	public static class TabTurnedOn extends InputMessage implements TabMessage,
			InterceptMessage {
	}

	public static class TabTurnedOff extends InputMessage implements
			TabMessage, InterceptMessage {
	}
}