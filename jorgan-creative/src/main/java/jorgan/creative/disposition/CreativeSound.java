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
package jorgan.creative.disposition;

import jorgan.disposition.GenericSound;
import jorgan.util.Null;

public class CreativeSound extends GenericSound {

	private String soundfont;

	private int bank;

	public String getSoundfont() {
		return soundfont;
	}

	public void setSoundfont(String soundfont) {
		if (!Null.safeEquals(this.soundfont, soundfont)) {
			String oldSoundfont = this.soundfont;
			
			this.soundfont = soundfont;

			fireChange(new UndoablePropertyChange(oldSoundfont, soundfont));
		}
	}

	public int getBank() {
		return bank;
	}

	public void setBank(int bank) {
		if (bank != this.bank) {
			int oldBank = this.bank;
			
			this.bank = bank;

			fireChange(new UndoablePropertyChange(oldBank, this.bank));
		}
	}
}