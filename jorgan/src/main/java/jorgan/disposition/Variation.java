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
 * A variation.
 */
public class Variation extends Registratable implements SoundEffect {

  private int program = 0;
  private int bank    = 0;

  public void setProgram(int program) {
    if (program < 0 || program > 127) {
      throw new IllegalArgumentException("program '" + program + "'");
    }

    this.program = program;

    fireElementChanged(true);
  }

  public int getProgram() {
    return program;
  }

  public void setBank(int bank) {
    if (bank < 0 || bank > 127) {
      throw new IllegalArgumentException("bank '" + bank + "'");
    }

    this.bank = bank;

    fireElementChanged(true);
  }

  public int getBank() {
    return bank;
  }
}