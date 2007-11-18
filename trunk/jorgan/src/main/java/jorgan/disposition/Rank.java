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

/**
 * A rank.
 */
public class Rank extends Element implements Engaging {

	private String device;

	private Matcher channels = new Matcher();

	private int delay = 0;

	public Rank() {
		addMessage(new Engaged().pattern("176, 121, 0"));
		addMessage(new Engaged().pattern("176, 0, 0"));
		addMessage(new Engaged().pattern("192, 0, 0"));
		addMessage(new Disengaged().pattern("176, 123, 0"));
		addMessage(new Played().pattern("144, pitch:0-127, velocity:0-127"));
		addMessage(new Muted().pattern("128, pitch:0-127, 0"));
	}

	public void setProgram(int program) {
		addMessage(new Engaged().pattern("192, " + program + ", 0"));
	}

	public int getProgram() {
		// TODO
		return -1;
	}

	@Override
	protected boolean canReference(Class clazz) {
		return SoundEffect.class.isAssignableFrom(clazz);
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;

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

	public Matcher getChannels() {
		return channels;
	}

	public void setChannels(Matcher channels) {
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

	public List<Class<? extends Matcher>> getMessageClasses() {
		List<Class<? extends Matcher>> names = super.getMessageClasses();

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

		public transient int pitch;

		public transient int velocity;
	}

	public static class Muted extends OutputMessage {

		public transient int pitch;
	}
}