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

import java.util.Set;

import jorgan.disposition.Output.OutputMessage;

/**
 * A rank.
 */
public class Rank extends Element implements Engageable, Console.Referenceable {

	private String channel = "";

	private int delay = 0;

	public Rank() {
		// control change, bank select, 0
		addMessage(new Engaged().change("set 176", "set 0", "set 0"));
		// program change, 0, -
		addMessage(new Engaged().change("set 192", "set 0", ""));
		// control change, reset all, -
		addMessage(new Disengaged().change("set 176", "set 121", ""));
		// control change, all notes off, -
		addMessage(new Disengaged().change("set 176", "set 123", ""));
		// note on, pitch, velocity
		addMessage(new NotePlayed().change("set 144", "set pitch",
				"set velocity"));
		// note off, pitch, -
		addMessage(new NoteMuted().change("set 128", "set pitch", ""));
	}

	/**
	 * Convenience method to set the midi program.
	 * 
	 * @param program
	 *            program to set
	 */
	public void setProgram(int program) {
		Engaged engaged = getProgramChange();
		if (engaged == null) {
			engaged = new Engaged();
			addMessage(engaged);
		}
		// program change, 0, -
		engaged.change("set 192", "set " + program, "");
	}

	/**
	 * Convenience method to get the midi program.
	 * 
	 * @return the program
	 */
	public int getProgram() {
		Engaged engaged = getProgramChange();
		if (engaged != null) {
			try {
				return Integer.parseInt(engaged.getData1().substring(
						"set ".length()));
			} catch (Exception ignore) {
			}
		}
		return 0;
	}

	/**
	 * Get the {@link Engaged} message sending a midi program change.
	 * 
	 * @return program change message
	 */
	private Engaged getProgramChange() {
		for (Engaged engaged : getMessages(Engaged.class)) {
			if ("set 192".equals(engaged.getStatus())) {
				return engaged;
			}
		}
		return null;
	}

	@Override
	protected boolean canReference(Class<? extends Element> clazz) {
		return Filter.class.isAssignableFrom(clazz)
				|| Sound.class.isAssignableFrom(clazz);
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		if (delay < 0) {
			throw new IllegalArgumentException("delay '" + delay + "'");
		}
		this.delay = delay;

		fireChanged(true);
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		if (channel == null) {
			throw new IllegalArgumentException("channel must not be null");
		}

		this.channel = channel;

		fireChanged(true);
	}

	/**
	 * Is this element angaged through a referencing {@link Stop}.
	 * 
	 * @return <code>true</code> if engaged
	 * 
	 * @see Stop#plays(Rank)
	 */
	public boolean isEngaged() {
		if (getOrgan() != null) {
			for (Stop stop : getOrgan().getReferrer(this, Stop.class)) {
				if (stop.plays(this)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * If a referring {@link Stop} changes, {@link #isEngaged()} might change
	 * too.
	 * 
	 * @see #isEngaged()
	 */
	@Override
	public void referrerChanged(Element element) {
		if (element instanceof Stop) {
			fireChanged(false);
		}
	}

	public Set<Class<? extends Message>> getMessageClasses() {
		Set<Class<? extends Message>> names = super.getMessageClasses();

		names.add(Engaged.class);
		names.add(Disengaged.class);
		names.add(NotePlayed.class);
		names.add(NoteMuted.class);

		return names;
	}

	public static class Engaged extends OutputMessage {
	}

	public static class NotePlayed extends OutputMessage {

		public static final String VELOCITY = "velocity";

		public static final String PITCH = "pitch";
	}

	public static class NoteMuted extends OutputMessage {

		public static final String PITCH = "pitch";
	}

	public static class Disengaged extends OutputMessage {
	}
}