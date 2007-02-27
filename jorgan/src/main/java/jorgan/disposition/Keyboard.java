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

/**
 * A keyboard.
 * 
 * TODO extends:
 * 
 * @see jorgan.disposition.Activateable
 */
public class Keyboard extends Element {

	public static final int COMMAND_NOTE_ON = 144;

	public static final int COMMAND_POLY_PRESSURE = 160;

	private String device = null;

	private Key from = null;

	private Key to = null;

	private int transpose = 0;

	private int channel = 0;

	private int command = COMMAND_NOTE_ON;

	private int threshold = 0;

	protected boolean canReference(Class clazz) {
		return Keyable.class.isAssignableFrom(clazz);
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;

		fireElementChanged(true);
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		if (channel < 0 || channel > 15) {
			throw new IllegalArgumentException("channel '" + channel + "'");
		}
		this.channel = channel;

		fireElementChanged(true);
	}

	public void setTranspose(int transpose) {
		this.transpose = transpose;

		fireElementChanged(true);
	}

	public int getTranspose() {
		return transpose;
	}

	public void setFrom(Key from) {
		this.from = from;

		fireElementChanged(true);
	}

	public void setTo(Key to) {
		this.to = to;

		fireElementChanged(true);
	}

	public Key getFrom() {
		return from;
	}

	public Key getTo() {
		return to;
	}

	public int getCommand() {
		return command;
	}

	public int getThreshold() {
		return threshold;
	}

	public void setCommand(int command) {
		if (command != COMMAND_NOTE_ON && command != COMMAND_POLY_PRESSURE) {
			throw new IllegalArgumentException("command '" + command + "'");
		}
		this.command = command;

		fireElementChanged(true);
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;

		fireElementChanged(true);
	}
}