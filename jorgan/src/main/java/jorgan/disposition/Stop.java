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
 * A stop.
 */
public class Stop extends Keyable {

    public static final int DEFAULT_PAN = 64;

    public static final int DEFAULT_VOLUME = 100;

    public static final int DEFAULT_BEND = 64;

    /**
     * Allocate a sound on the first referenced {@link SoundSource} that is
     * available.
     */
    public static final int ALLOCATION_FIRST_AVAILABLE = 0;

    /**
     * Allocate a sound on each referenced {@link SoundSource} that is
     * available.
     */
    public static final int ALLOCATION_ALL_AVAILABLE = 1;

    /**
     * How should a sound be allocated.
     * 
     * @see #ALLOCATION_FIRST
     * @see #ALLOCATION_ALL
     * @see #ALLOCATION_PITCH
     */
    private int allocation = ALLOCATION_FIRST_AVAILABLE;

    private int program = 0;

    private int pan = DEFAULT_PAN;

    private int volume = DEFAULT_VOLUME;

    private int bend = DEFAULT_BEND;

    protected boolean canReference(Class clazz) {
        return SoundEffect.class.isAssignableFrom(clazz)
                || SoundSource.class.isAssignableFrom(clazz);
    }

    public int getAllocation() {
        return allocation;
    }

    public int getProgram() {
        return program;
    }

    public int getPan() {
        return pan;
    }

    public int getBend() {
        return bend;
    }

    public int getVolume() {
        return volume;
    }

    public void setAllocation(int allocation) {
        if (allocation != ALLOCATION_FIRST_AVAILABLE
                && allocation != ALLOCATION_ALL_AVAILABLE) {
            throw new IllegalArgumentException("allocation '" + allocation
                    + "'");
        }
        this.allocation = allocation;

        fireElementChanged(true);
    }

    public void setProgram(int program) {
        if (program < 0 || program > 127) {
            throw new IllegalArgumentException("program '" + program + "'");
        }
        this.program = program;

        fireElementChanged(true);
    }

    public void setPan(int pan) {
        if (pan < 0 || pan > 127) {
            throw new IllegalArgumentException("pan '" + pan + "'");
        }
        this.pan = pan;

        fireElementChanged(true);
    }

    public void setBend(int bend) {
        if (bend < 0 || bend > 127) {
            throw new IllegalArgumentException("bend '" + bend + "'");
        }
        this.bend = bend;

        fireElementChanged(true);
    }

    public void setVolume(int volume) {
        if (volume < 0 || volume > 127) {
            throw new IllegalArgumentException("volume '" + volume + "'");
        }
        this.volume = volume;

        fireElementChanged(true);
    }
}