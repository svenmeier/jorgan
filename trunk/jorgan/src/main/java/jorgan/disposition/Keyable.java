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
 * A keyable.
 */
public abstract class Keyable extends Activateable {

    public static final int ACTION_STRAIGHT = 0;

    public static final int ACTION_PITCH_CONSTANT = 1;

    public static final int ACTION_PITCH_HIGHEST = 2;

    public static final int ACTION_PITCH_LOWEST = 3;

    public static final int ACTION_SUSTAIN = 4;

    public static final int ACTION_SOSTENUTO = 5;

    public static final int ACTION_INVERSE = 6;

    public static final int DEFAULT_TRANSPOSE = 0;

    public static final int DEFAULT_VELOCITY = 100;

    private int velocity = DEFAULT_VELOCITY;

    private int action = ACTION_STRAIGHT;

    private int transpose = DEFAULT_TRANSPOSE;

    public int getTranspose() {
        return transpose;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        if (action < ACTION_STRAIGHT || action > ACTION_INVERSE) {
            throw new IllegalArgumentException("pitch '" + action + "'");
        }
        this.action = action;

        fireElementChanged(true);
    }

    public void setTranspose(int transpose) {
        this.transpose = transpose;

        fireElementChanged(true);
    }

    public int getVelocity() {
        return velocity;
    }

    public void setVelocity(int velocity) {
        if (velocity < 0 || velocity > 127) {
            throw new IllegalArgumentException("velocity '" + velocity + "'");
        }
        this.velocity = velocity;

        fireElementChanged(true);
    }
}