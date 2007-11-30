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
 * A rank.
 */
public class Rank extends Element implements Engageable, Displayable {

	private String output;

	private String channels = "0-15";

	private int delay = 0;

	public Rank() {
		addMessage(new Engaged().init("176", "0", "0"));
		addMessage(new Engaged().init("192", "0", "0"));
		addMessage(new Disengaged().init("176", "121", "0"));
		addMessage(new Disengaged().init("176", "123", "0"));
		addMessage(new Played());
		addMessage(new Muted());
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
		engaged.init("192", "" + program, "0");
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
				return Integer.parseInt(engaged.getData1());
			} catch (NumberFormatException ignore) {
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
			if ("192".equals(engaged.getStatus())) {
				return engaged;
			}
		}
		return null;
	}

	@Override
	protected Class<?> references() {
		return Filter.class;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;

		fireElementChanged(true);
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		if (delay < 0) {
			throw new IllegalArgumentException("delay '" + delay + "'");
		}
		this.delay = delay;

		fireElementChanged(true);
	}

	public String getChannels() {
		return channels;
	}

	public void setChannels(String channels) {
		if (channels == null) {
			throw new IllegalArgumentException("channels must not be null");
		}

		this.channels = channels;

		fireElementChanged(true);
	}

	/**
	 * Is this element angaged through a referencing {@link Stop}.
	 * 
	 * @return <code>true</code> if engaged
	 * 
	 * @see Stop#plays(Rank)
	 */
	public boolean isEngaged() {
		for (Stop stop : getReferrer(Stop.class)) {
			if (stop.plays(this)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * If a referring {@link Stop} changes, this element changes too.
	 * 
	 * @see #isEngaged()
	 */
	@Override
	public void referrerChanged(Element element) {
		if (element instanceof Stop) {
			fireElementChanged(false);
		}
	}

	public List<Class<? extends Message>> getMessageClasses() {
		List<Class<? extends Message>> names = super.getMessageClasses();

		names.add(Engaged.class);
		names.add(Disengaged.class);
		names.add(Played.class);
		names.add(Muted.class);

		return names;
	}

	public static class Engaged extends OutputMessage {
	}

	public static class Disengaged extends OutputMessage {
	}

	public static class Played extends OutputMessage {

		public static final String VELOCITY = "velocity";

		public static final String PITCH = "pitch";

		{
			init("144", "get pitch", "get velocity");
		}
	}

	public static class Muted extends OutputMessage {

		public static final String PITCH = "pitch";

		{
			init("128", "get pitch", "");
		}
	}
}